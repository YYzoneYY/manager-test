package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.DataScopeSelf;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.ConstructionPersonnelEntity;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizPlanDto;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.domain.excel.BizProJson;
import com.ruoyi.system.domain.excel.CustomCellWriteHandler;
import com.ruoyi.system.domain.excel.MaxValueCellColorHandler;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工程填报记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Service
public class BizProjectRecordServiceImpl extends MPJBaseServiceImpl<BizProjectRecordMapper, BizProjectRecord> implements IBizProjectRecordService
{
    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;


    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private BizProjectAuditMapper bizProjectAuditMapper;

    @Autowired
    private BizVideoMapper bizVideoMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private BizDrillRecordMapper bizDrillRecordMapper;

    @Autowired
    private TunnelMapper tunnelMapper; ;

    @Autowired
    private  BizWorkfaceMapper bizWorkfaceMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private MiningFootageMapper miningFootageMapper;
    @Autowired
    private BizTravePointMapper bizTravePointMapper;

//    @Autowired
//    private MinioClient getMinioClient;



    @DataScopeSelf
    public MPage<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto, Pagination pagination){

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecordListVo.class)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(dto.getConstructUnitId()!=null,BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getTunnelId() != null,BizProjectRecord::getTunnelId,dto.getTunnelId())
                .eq(dto.getDrillType()!=null,BizProjectRecord::getDrillType,dto.getDrillType())
                .eq(dto.getConstructShiftId()!=null,BizProjectRecord::getConstructShiftId,dto.getConstructShiftId())
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus())
                .between(StrUtil.isNotEmpty(dto.getStartTime()),BizProjectRecord::getConstructTime,DateUtils.parseDate(dto.getStartTime()),DateUtils.parseDate(dto.getEndTime()))
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus());
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination , queryWrapper);
        return new MPage<>(sss);
    }


    @Override
    public MPage<BizProjectRecordListVo> selectproList(BasePermission permission, BizProjectRecordDto dto , Pagination pagination) {

        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .between(BizProjectRecord::getConstructTime,startDate,currentDate);
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination, queryWrapper);
        return new MPage<>(sss);
    }


    @Override
    public BizProStatsVo statsProject(BasePermission permission, BizProjectRecordDto dto) {
        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(TunnelEntity::getTunnelName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(BizProjectRecord::getConstructType,BizBaseConstant.CONSTRUCT_TYPE_J)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId, BizProjectRecord::getTunnelId)
                .groupBy(BizProjectRecord::getTunnelId);
        List<Map<String,Object>> locationMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper);

        MPJLambdaWrapper<BizProjectRecord> queryWrapper1 = new MPJLambdaWrapper<>();
        queryWrapper1
                .selectAs(BizWorkface::getWorkfaceName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(BizProjectRecord::getConstructType,BizBaseConstant.CONSTRUCT_TYPE_H)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId, BizProjectRecord::getTunnelId)
                .groupBy(BizProjectRecord::getTunnelId);
        List<Map<String,Object>> huicaiMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper1);
        for (Map<String, Object> stringObjectMap : huicaiMap) {
            String  name = "回采"+stringObjectMap.get("name").toString();
            Object  value = stringObjectMap.get("value");
            stringObjectMap.put(name,value);
        }
        locationMap.addAll(huicaiMap);


        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizProjectRecord::getDrillType)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .groupBy(BizProjectRecord::getDrillType);
        List<Map<String,Object>> drillTypeMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();

        queryWrapper
                .selectAs(ConstructionUnitEntity::getConstructionUnitName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .groupBy(BizProjectRecord::getConstructUnitId);
        List<Map<String,Object>> unitMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper);

        BizProStatsVo vo = new BizProStatsVo();
        vo.setLocationMap(locationMap).setTypeMap(drillTypeMap).setUnitMap(unitMap);
        return vo;
    }


    @Override
    public MPage<BizProjectRecordPaibanVo> selectPaiList(BasePermission permission, BizProjectRecordDto dto, Pagination pagination) {
        Date currentDate = null;
        Date startDate = null;
        if(dto.getDayNum() != null ){
            currentDate = new Date();
            startDate = DateUtil.offsetDay(currentDate, -dto.getDayNum());
        }else {
            currentDate = DateUtils.parseDate(dto.getStartTime());
            startDate = DateUtils.parseDate(dto.getEndTime());
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizDrillRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(ConstructionPersonnelEntity::getName,"worker")
                .selectAs(TunnelEntity::getTunnelName,"tunnelName")
                .selectAs(BizWorkface::getWorkfaceName,"workfaceName")
                .selectAs(ConstructionUnitEntity::getConstructionUnitId, ConstructionUnitEntity::getConstructionUnitName)
                .select(BizProjectRecord::getDrillNum,BizProjectRecord::getConstructTime,BizProjectRecord::getSteelBeltStart,BizProjectRecord::getSteelBeltEnd)
                .select(BizDrillRecord::getRealDeep,BizDrillRecord::getDiameter,BizDrillRecord::getRemark)
                .innerJoin(BizProjectRecord.class,BizProjectRecord::getProjectId,BizDrillRecord::getProjectId)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getTunnelId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId)
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .leftJoin(ConstructionPersonnelEntity.class,ConstructionPersonnelEntity::getConstructionPersonnelId,BizProjectRecord::getWorker)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_N);

        IPage<BizProjectRecordPaibanVo> maps = bizDrillRecordMapper.selectJoinPage(pagination,BizProjectRecordPaibanVo.class,queryWrapper);
        return new MPage<>(maps);
    }

    @Override
    public MPage<Map<String, Object>> monitorProject(BizPlanDto dto, Pagination pagination) {

//todo 计划还没写        SysDept代表计划
//        MPJLambdaWrapper<SysDept> queryWrapper = new MPJLambdaWrapper<>();
//        queryWrapper
//                .selectAll(SysDept.class)
//                .selectCount(BizDrillRecord::getDrillRecordId,"drillNum")
//                .selectSum(BizDrillRecord::getRealDeep,"deepNum")
//                .leftJoin(BizDrillRecord.class,BizDrillRecord::getPlanId,SysDept::getPlanId)
//                .eq(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
//                .eq(SysDept::getDelFlag,BizBaseConstant.DELFLAG_N)
//                .eq(StrUtil.isNotBlank(dto.getSearchDate()),计划时间,dto.getSearchDate())
//                .eq(dto.getDrillType() != null ,类型,dto.getDrillType())
//                .eq(dto.getTunnelId() != null , 施工地点 , dto.getTunnelId())
//                .groupBy(SysDept::getplanId);
//
//        IPage<Map<String,Object>> ps =  sysDeptMapper.pagemap(pagination,queryWrapper);
//        return new MPage<>(ss);
        return new MPage<>(null);
    }

    @Override
    public BizProjectRecordVo selectById(Long bizProjectRecordId) {

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
//todo                .leftJoin(,BizProjectRecord::getConstructUnitId)
//               .leftJoin(,BizProjectRecord::getConstructLocationId)
//               .leftJoin(,BizProjectRecord::getConstructShiftId)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructLocationName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructUnitName)
//                .select(BizProjectRecord::getProjectId,BizProjectRecordVo::getConstructShiftName)
                .leftJoin(BizMine.class,BizMine::getMineId,BizProjectRecord::getDeptId)
                .eq(BizProjectRecord::getProjectId,bizProjectRecordId);
        return  bizProjectRecordMapper.selectJoinOne(BizProjectRecordVo.class, queryWrapper);
    }

    @Override
    public List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord) {
        return null;
    }

    @Override
    public int saveRecord(BizProjectRecordAddDto dto) {
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);

        long  projectId =  this.getBaseMapper().insert(entity);
        dto.getDrillRecords().forEach(drillRecord -> {
            drillRecord.setProjectId(projectId);
            bizDrillRecordMapper.insert(drillRecord);
        });
        dto.getVideos().forEach(bizVideo -> {
            bizVideo.setProjectId(projectId);
            bizVideoMapper.insert(bizVideo);
        });
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg("提交")
                .setLevel("AUTHOR")
                .setStatus(0);
        bizProjectAuditMapper.insert(audit);
        return 1;
    }


    @Override
    public int updateRecord(BizProjectRecordAddDto dto) {
        this.updateById(dto);
        UpdateWrapper<BizDrillRecord> drillUpdateWrapper= new UpdateWrapper<>();
        drillUpdateWrapper.lambda().eq(BizDrillRecord::getProjectId,dto.getProjectId()).set(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizDrillRecordMapper.update(null,drillUpdateWrapper);
        UpdateWrapper<BizVideo> videoUpdateWrapper= new UpdateWrapper<>();
        videoUpdateWrapper.lambda().eq(BizVideo::getProjectId,dto.getProjectId()).set(BizVideo::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizVideoMapper.update(null,videoUpdateWrapper);

        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            dto.getDrillRecords().forEach(drillRecord -> {
                drillRecord.setDrillRecordId(null);
                drillRecord.setProjectId(dto.getProjectId());
                bizDrillRecordMapper.insert(drillRecord);
            });
        }
        if(dto.getVideos() != null && dto.getVideos().size() > 0){
            dto.getVideos().forEach(bizVideo -> {
                bizVideo.setVideoId(null);
                bizVideo.setProjectId(dto.getProjectId());
                bizVideoMapper.insert(bizVideo);
            });
        }

        return 1;
    }

    @Override
    public int updateRecordById(BizProjectRecordAddDto dto) {
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);
        bizProjectRecordMapper.updateById(entity);
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg("提交")
                .setLevel("AUTHOR")
                .setStatus(0);
        bizProjectAuditMapper.insert(audit);
        return 1;
    }

    @Override
    public int firstAudit(BizProjectRecordDto dto) {
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg(dto.getMsg())
                .setLevel("TEAM")
                .setStatus(dto.getAudit());
        bizProjectAuditMapper.insert(audit);
        BizProjectRecord entity = new BizProjectRecord();
        entity.setProjectId(dto.getProjectId())
                        .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_TEAM_PASS:BizBaseConstant.FILL_STATUS_TEAM_BACK);
        bizProjectRecordMapper.updateById(entity);
        return 1;
    }


    @Override
    public int secondAudit(BizProjectRecordDto dto) {
        BizProjectAudit audit = new BizProjectAudit();
        audit.setProjectId(dto.getProjectId())
                .setMsg(dto.getMsg())
                .setLevel("DEPT")
                .setStatus(dto.getAudit());
        bizProjectAuditMapper.insert(audit);
        BizProjectRecord entity = new BizProjectRecord();
        entity.setProjectId(dto.getProjectId())
                .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_DEPART_PASS:BizBaseConstant.FILL_STATUS_DEPART_BACK);
        bizProjectRecordMapper.updateById(entity);
        return 1;
    }


    @Override
    public void getReport(BizProjectRecordDto1 dto, HttpServletResponse response) {


    }
    /**
     * 多重施法
     * @return
     */
    private List<List<String>> head(String title) {
        List<List<String>> headList2D = new ArrayList<>();

        return headList2D;

    }


    @Override
    public void getDayReport(Long mineId, String statsDate, Long deptId, HttpServletResponse response) throws UnsupportedEncodingException {

        System.out.println("list = ");
    }

    public static void main(String[] args) {
       List<BizProjectDayRecordMap> list = new ArrayList<>();
       BizProjectDayRecordMap bizProjectDayRecordMap1 = new BizProjectDayRecordMap();
       bizProjectDayRecordMap1.setProjectId(1L).setNo(1);
        BizProjectDayRecordMap bizProjectDayRecordMap2 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap2.setProjectId(2L).setNo(2);
        BizProjectDayRecordMap bizProjectDayRecordMap3 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap3.setProjectId(3L).setNo(3);
        BizProjectDayRecordMap bizProjectDayRecordMap4 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap4.setProjectId(4L).setNo(4);
        BizProjectDayRecordMap bizProjectDayRecordMap5 = new BizProjectDayRecordMap();
        bizProjectDayRecordMap5.setProjectId(1L).setNo(5);

        list.add(bizProjectDayRecordMap1);
        list.add(bizProjectDayRecordMap2);
        list.add(bizProjectDayRecordMap3);
        list.add(bizProjectDayRecordMap4);
        list.add(bizProjectDayRecordMap5);
         List<BizProjectDayRecordMap> s  = list.stream()
                 .collect(Collectors.toMap( BizProjectDayRecordMap::getProjectId, record  -> record,  (existing, replacement) -> existing)) .values() .stream()  .collect(Collectors.toList());
        System.out.println("s = " + s);
    }

    private MPJLambdaWrapper<BizProjectRecord> getDirectionWrapper(String direction){
        MPJLambdaWrapper<BizProjectRecord> projectQueryWrapper = new MPJLambdaWrapper<>();
        projectQueryWrapper
                .select(ConstructionUnitEntity::getConstructionUnitName)
                .select(BizWorkface::getWorkfaceName)
                .select(BizDrillRecord::getDirection,BizDrillRecord::getRealDeep)
                .select(BizProjectRecord::getConstructUnitId,BizProjectRecord::getTunnelId,BizProjectRecord::getRemark)
                .innerJoin(BizDrillRecord.class,BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .innerJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getTunnelId);
//                .eq(BizDrillRecord::getDirection,direction)
//                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_LDPR)
//                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N);
        return projectQueryWrapper;

    }

    private List<String> addData(List<String> list,BizProjectCDMAP map){

        List<BizProJson> jsons =  JSONUtil.toList(map.getCrumbWeight(),BizProJson.class);
        if (jsons != null && jsons.size() > 0){
            for (BizProJson json : jsons){
                list.add(json.getValue());
            }
        }else {
            list = addBlank(list,14);
        }
        return list;
    }

    private List<String> addBlank(List<String> list,int n){
        for(int i=0;i<n;i++){
            list.add("");
        }
        return list;
    }

    @Override
    public void get444(HttpServletResponse response) throws IOException {

        //查询工作面 下的 巷道
        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda()
                .select(TunnelEntity::getTunnelId)
                .eq(TunnelEntity::getWorkFaceId,"1")
                .eq(TunnelEntity::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<TunnelEntity> tunnelList = tunnelMapper.selectList(tunnelQueryWrapper);
        List<Long> tunnelIds = new ArrayList<>();
        Assert.isTrue(tunnelList != null && tunnelList.size() > 0, "没有巷道");
        tunnelIds = tunnelList.stream().map(TunnelEntity::getTunnelId).collect(Collectors.toList());

        //todo 预警值还没有表 设为
        List<String>  alarmLabels = Arrays.asList("日期","1m", "2m", "3m", "4m", "5m", "6m", "7m", "8m", "9m","10m","11m","12m","13m","14m","15m");
        List<String>  alarmValues = Arrays.asList("预警值","3.1", "3.1", "3.9", "3.1", "3.4", "3.1", "3.2", "3.1", "3.1","3.5","3.1","3.1","3.6","3.1","3.1");
//        alarmLabels.add(0,);
//        alarmValues.add(0,);

        MPJLambdaWrapper<BizProjectRecord> projectRecordQueryWrapper = new MPJLambdaWrapper<>();

        projectRecordQueryWrapper
                .selectFunc(() -> "DATE_FORMAT(%s, '%%Y-%%m-%%d %%H:%%i:%%s')", BizProjectRecord::getConstructTime)
                .select(BizDrillRecord::getCrumbWeight)
                .select(BizProjectRecord::getConstructRange,BizProjectRecord::getTunnelId,BizProjectRecord::getTravePointId)
                .in(BizProjectRecord::getTunnelId,tunnelIds)
                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_CD)
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .innerJoin(BizDrillRecord.class,BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId);
        List<BizProjectCDMAP> cdmaps = bizProjectRecordMapper.selectJoinList(BizProjectCDMAP.class,projectRecordQueryWrapper);
        Assert.isTrue(cdmaps != null && cdmaps.size() > 0, "没有记录");

        //先根据 巷道分组
        Map<Long, List<BizProjectCDMAP>> groupedByTunnelId = cdmaps.stream()
                .collect(Collectors.groupingBy(BizProjectCDMAP::getTunnelId));

        groupedByTunnelId.forEach((tunnelId, groupbytunnel) -> {
                    System.out.println("TunnelId: " + tunnelId);
                    Map<String, List<BizProjectCDMAP>> groupbyTime = groupbytunnel
                            .stream().collect(Collectors.groupingBy(BizProjectCDMAP::getConstructTime));

                    List<String> distinctConstructRanges = groupbytunnel.stream()
                            .map(BizProjectCDMAP::getConstructRange) // 提取 constructRange 字段
                            .filter(range -> range != null && !range.isEmpty()) // 过滤 null 和空值
                            .distinct() // 去重
                            .collect(Collectors.toList());

                    QueryWrapper<BizProjectRecord> rangePointMapper = new QueryWrapper<>();
                    rangePointMapper.lambda()
                            .select(BizProjectRecord::getConstructRange, BizProjectRecord::getTravePointId)
                            .eq(BizProjectRecord::getTunnelId, tunnelId)
                            .eq(BizProjectRecord::getDelFlag, BizBaseConstant.DELFLAG_N);
                    //todo 需要的话 加日期筛选
                    List<BizProjectRecord> rangePoints = bizProjectRecordMapper.selectList(rangePointMapper);

                    List<List<String>> excelData = new ArrayList<>();
                    groupbyTime.forEach((dateTime, group) -> {
                        List<String> rows = new ArrayList<>();
                        rows.add(dateTime);
                        for (BizProjectRecord rangePoint : rangePoints) {
                            List<BizProjectCDMAP> filteredRecords = group.stream().filter(record -> rangePoint.getConstructRange().equals(record.getConstructRange()) && record.getTravePointId() == rangePoint.getTravePointId()).collect(Collectors.toList());
                            if (filteredRecords != null && filteredRecords.size() > 0) {
                                addBlank(rows, 14);
                            } else {
                                //todo 默认一个时间,某个导线点 某个距离 只有一个 填报记录
                                addData(rows, filteredRecords.get(0));
                            }
                            rows.add(dateTime);
                        }
                        excelData.add(rows);
                    });




                });
    }



    @Override
    public void sss555(HttpServletResponse response) {
// 创建 List<List<String>> 来存储数据
        List<List<String>> rows = new ArrayList<>();

        // 第1行数据
        List<String> row1 = new ArrayList<>();
        row1.add("5月19日");
        row1.add("2.1");
        row1.add("2.1");
        row1.add("2.3");
        row1.add("2.1");
        row1.add("2.4");
        row1.add("2.2");
        row1.add("2.4");
        row1.add("2.7");
        row1.add("2.5");
        row1.add("2.6");
        row1.add("2.2");
        row1.add("2.7");
        row1.add("2.3");
        row1.add("2.3");
        row1.add("5月19日");
        row1.add("1.8");
        row1.add("2");
        row1.add("2.3");
        row1.add("2.2");
        row1.add("2.4");
        row1.add("2.4");
        row1.add("2.3");
        row1.add("2.5");
        row1.add("2.2");
        row1.add("2.3");
        row1.add("2.6");
        row1.add("2.7");
        row1.add("2.6");
        row1.add("2.5");

        // 第2行数据
        List<String> row2 = new ArrayList<>();
        row2.add("5月21日");
        row2.add("2.1");
        row2.add("2.2");
        row2.add("2");
        row2.add("1.9");
        row2.add("2.1");
        row2.add("2.3");
        row2.add("1.9");
        row2.add("1.9");
        row2.add("2.1");
        row2.add("2.3");
        row2.add("1.8");
        row2.add("2.5");
        row2.add("2.6");
        row2.add("2.1");
        row2.add("5月21日");
        row2.add("1.7");
        row2.add("2");
        row2.add("2");
        row2.add("1.7");
        row2.add("2.2");
        row2.add("2.1");
        row2.add("2.1");
        row2.add("2.4");
        row2.add("2.3");
        row2.add("2.3");
        row2.add("2.5");
        row2.add("2.2");
        row2.add("2.2");
        row2.add("2.4");

        // 第3行数据
        List<String> row3 = new ArrayList<>();
        row3.add("5月25日");
        row3.add("2.2");
        row3.add("1.8");
        row3.add("2.2");
        row3.add("1.9");
        row3.add("1.9");
        row3.add("2");
        row3.add("2");
        row3.add("2");
        row3.add("2.1");
        row3.add("2.2");
        row3.add("2.2");
        row3.add("2.3");
        row3.add("2.3");
        row3.add("2.6");
        row3.add("5月25日");
        row3.add("1.9");
        row3.add("2");
        row3.add("2.2");
        row3.add("2");
        row3.add("2");
        row3.add("2.1");
        row3.add("2");
        row3.add("2");
        row3.add("2.1");
        row3.add("2");
        row3.add("2.2");
        row3.add("2.1");
        row3.add("2.1");
        row3.add("2.4");

        // 将每一行数据加入 rows 列表
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows", rows);
        jsonObject.put("sheetName", "1");
        List<String> ss = new ArrayList<>();
        ss.add("assad0");
        ss.add("assad1");
        jsonObject.put("tabelNames", ss );
        JSONArray array = new JSONArray();
        array.add(jsonObject);

    try {
        sss(response,array);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

}


    private void sss(HttpServletResponse response, JSONArray array) throws IOException {

        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + "ssss" + ".xlsx");
        List<List<String>> listsss = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<String>();
        head0.add("字符串" );
        List<String> head1 = new ArrayList<String>();
        head1.add("数字");
        List<String> head2 = new ArrayList<String>();
        head2.add("日期");
        listsss.add(head0);
        listsss.add(head1);
        listsss.add(head2);

        ExcelWriterBuilder excelWriter = EasyExcel.write(response.getOutputStream()).registerWriteHandler(new CustomCellWriteHandler(15)).head(listsss);
//                .relativeHeadRowIndex(1);
        WriteCellData<String> richTest = new WriteCellData<>();
        if(array != null && array.size() > 0){
            for (Object o : array) {
                String sheetName = JSONUtil.parseObj(o).getStr("sheetName");
                List<String> tabelNames = JSONUtil.parseObj(o).getBeanList("tabelNames",String.class);
                List rows = JSONUtil.parseObj(o).getBeanList("rows",List.class);
                List<List<String>> excelData = new ArrayList<>();
                for (Object row : rows) {
                    excelData.add(JSONUtil.toList(JSONUtil.parseArray(row),String.class));
                }


                excelWriter.sheet(sheetName).registerWriteHandler(new MaxValueCellColorHandler())
                        .doWrite(excelData);
            }
            excelWriter.build().finish();
        }
    }
}






////                    WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
//                    CustomDataWriteHandler dataWriteHandler1 = new CustomDataWriteHandler(1, 1);
//                    excelWriter.sheet("s").doWrite(dataRanges);
//                    excelWriter.registerWriteHandler(new MaxValueCellColorHandler()).registerWriteHandler(dataWriteHandler1);
//
//                    CustomDataWriteHandler dataWriteHandler2 = new CustomDataWriteHandler(1, 20);
//
//                    excelWriter.sheet("s2").doWrite(dataRanges);
//                    excelWriter.registerWriteHandler(dataWriteHandler2);
//
//
//                    excelWriter.build().finish();

//                    EasyExcel.write(response.getOutputStream())
//                            .sheet("1");
//                            .registerWriteHandler(new MaxValueCellColorHandler())
//                            .doWrite(dataRanges);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//        });




        //地点



//        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(resource.getURI().getPath()).build()) {
//            WriteSheet writeSheet = EasyExcel.writerSheet().build();
//            // 这里注意 入参用了forceNewRow 代表在写入list的时候不管list下面有没有空行 都会创建一行，然后下面的数据往后移动。默认 是false，会直接使用下一行，如果没有则创建。
//            // forceNewRow 如果设置了true,有个缺点 就是他会把所有的数据都放到内存了，所以慎用
//            // 简单的说 如果你的模板有list,且list不是最后一行，下面还有数据需要填充 就必须设置 forceNewRow=true 但是这个就会把所有数据放到内存 会很耗内存
//            // 如果数据量大 list不是最后一行 参照下一个
//            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
//            excelWriter.fill(excelList, fillConfig, writeSheet);
////            excelWriter.fill(excelList, fillConfig, writeSheet);
//            Map<String, Object> map = MapUtils.newHashMap();
//            map.put("statsDate", "2019年10月9日13:28:28");
//            map.put("deptName", 1000);
//            excelWriter.fill(map, writeSheet);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Map<String,Object> params1 = new HashMap<>();
//        params1.put("value",1);
//        Map<String,Object> params2 = new HashMap<>();
//        params2.put("value",2);
//        Map<String,Object> params3 = new HashMap<>();
//        params3.put("value",3);
//        List<Map<String,Object>> list = new ArrayList<>();
//        list.add(params1);
//        list.add(params2);
//        list.add(params3);
//
//
//
//        Map<String,Object> paramsd1 = new HashMap<>();
//        paramsd1.put("date","2024-1");
//        Map<String,Object> paramsd2 = new HashMap<>();
//        paramsd2.put("date","2024-2");
//        Map<String,Object> paramsd3 = new HashMap<>();
//        paramsd3.put("date","2024-3");
//        List<Map<String,Object>> listd = new ArrayList<>();
//        listd.add(paramsd1);
//        listd.add(paramsd2);
//        listd.add(paramsd3);
//
//        List<List<String>> lo = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            List<String> listll = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
//            lo.add(listll);
//        }


        // 这里 需要指定写用哪个class去写

//        EasyExcel.write(response.getOutputStream(), DownloadData.class).sheet("模板").doWrite(data());

//        try  {
//            EasyExcel.write(response.getOutputStream()).sheet("1").doWrite(lo);
            // 这里注意 如果同一个sheet只要创建一次
//            WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
            // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
//            for (int i = 0; i < list.size(); i++) {
//                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
//                excelWriter.write(list, writeSheet);
//            }
//        }

//        // 方案1
//        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(templateFileName).build()) {
//            WriteSheet writeSheet = EasyExcel.writerSheet().build();
//            FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
//            // 如果有多个list 模板上必须有{前缀.} 这里的前缀就是 data1，然后多个list必须用 FillWrapper包裹
//            excelWriter.fill(new FillWrapper("data2", list), fillConfig, writeSheet);
////            excelWriter.fill(new FillWrapper("data1", data()), fillConfig, writeSheet);
//            excelWriter.fill(new FillWrapper("data1", listd), writeSheet);
////            excelWriter.fill(new FillWrapper("data2", data()), writeSheet);
////            excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
////            excelWriter.fill(new FillWrapper("data3", data()), writeSheet);
//
//            Map<String, Object> map = new HashMap<String, Object>();
//            //map.put("date", "2019年10月9日13:28:28");
//            map.put("date", new Date());
//
//            excelWriter.fill(map, writeSheet);
//        }
//    }









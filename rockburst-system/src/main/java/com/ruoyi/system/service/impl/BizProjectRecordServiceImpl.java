package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.annotation.DataScopeSelf;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.ConstructionUnitEntity;
import com.ruoyi.system.domain.dto.BizPlanDto;
import com.ruoyi.system.domain.dto.BizProjectRecordAddDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto;
import com.ruoyi.system.domain.dto.BizProjectRecordDto1;
import com.ruoyi.system.domain.vo.BizProStatsVo;
import com.ruoyi.system.domain.vo.BizProjectDayRecordMap;
import com.ruoyi.system.domain.vo.BizProjectRecordListVo;
import com.ruoyi.system.domain.vo.BizProjectRecordVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizProjectRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
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
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecord.class)
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

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
//                .selectAs(::getDeptName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .leftJoin(SysDept.class,SysDept::getDeptId,BizProjectRecord::getDeptId)
//                .leftJoin(BizProjectRecord::getConstructLocationId)
                .groupBy(BizProjectRecord::getDeptId);
        Map<String,Object> locationMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizProjectRecord::getDrillType)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .groupBy(BizProjectRecord::getDrillType);
        Map<String,Object> drillTypeMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();

        queryWrapper
//                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
//                .leftJoin(.class,,BizProjectRecord::getConstructUnitId)
                .between(BizProjectRecord::getConstructTime,startDate,currentDate)
                .groupBy(BizProjectRecord::getConstructUnitId);
        Map<String,Object> unitMap = bizProjectRecordMapper.selectJoinMap(queryWrapper);

        BizProStatsVo vo = new BizProStatsVo();
        vo.setLocationMap(unitMap).setTypeMap(drillTypeMap).setUnitMap(unitMap);
        return vo;
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


}








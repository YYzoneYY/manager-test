package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.dto.project.BizCardVDto;
import com.ruoyi.system.domain.dto.project.BizWashProofDto;
import com.ruoyi.system.domain.excel.BizProJson;
import com.ruoyi.system.domain.excel.ChartData;
import com.ruoyi.system.domain.excel.ChartDataAll;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 工程填报记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Transactional
@Service
@Slf4j
public class BizProjectRecordServiceImpl extends MPJBaseServiceImpl<BizProjectRecordMapper, BizProjectRecord> implements IBizProjectRecordService
{
    @Autowired
    private BizProjectRecordMapper bizProjectRecordMapper;

    @Autowired
    private SysUserMapper sysUserMapper;


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
    private IBizTravePointService bizTravePointService;

    @Autowired
    RelatesInfoMapper relatesInfoMapper;

    @Autowired
    ApachePoiLineChart11 apachePoiLineChart;

    @Autowired
    PlanMapper planMapper;

    @Autowired
    PlanService planService;
    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;
    @Autowired
    private BizPresetPointMapper bizPresetPointMapper;


    @DataScopeSelf
    public MPage<BizProjectRecordListVo> getlist(BasePermission permission, BizProjectRecordDto dto, Pagination pagination){

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecordListVo.class)
                .selectAs(BizProjectRecord::getStatus, BizProjectRecordListVo::getConstructLocation)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(dto.getConstructUnitId()!=null,BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getLocationId() != null,BizProjectRecord::getTunnelId,dto.getLocationId())
                .eq(dto.getDrillType() !=null,BizProjectRecord::getDrillType,dto.getDrillType())
                .eq(dto.getConstructShiftId()!=null,BizProjectRecord::getConstructShiftId,dto.getConstructShiftId())
                .between(StrUtil.isNotEmpty(dto.getStartTime()),BizProjectRecord::getConstructTime,DateUtils.parseDate(dto.getStartTime()),DateUtils.parseDate(dto.getEndTime()))
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus());
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination , queryWrapper);
        List<BizProjectRecordListVo> vos =  sss.getRecords();
        List<BizProjectRecordListVo> vv = new ArrayList<>();
        vv = BeanUtil.copyToList(vos,BizProjectRecordListVo.class);
        sss.setRecords(vv);
        return new MPage<>(sss);
    }




    @Override
    public MPage<BizProjectRecordListVo> selectproList(BasePermission permission, BizWashProofDto dto , Pagination pagination) {

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = getWrapper(permission,dto);
        queryWrapper
                .selectAll(BizProjectRecord.class);
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination, queryWrapper);
        List<BizProjectRecordListVo> vos =  sss.getRecords();
        List<BizProjectRecordListVo> vv = new ArrayList<>();
        vv = BeanUtil.copyToList(vos,BizProjectRecordListVo.class);
        sss.setRecords(vv);
        return new MPage<>(sss);
    }


    @Override
    public BizProStatsVo statsProject(BasePermission permission, BizWashProofDto dto) {

        MPJLambdaWrapper<BizProjectRecord> queryWrapper = getWrapper(permission, dto );
        queryWrapper
                .selectAs(TunnelEntity::getTunnelName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .eq(BizProjectRecord::getConstructType,BizBaseConstant.CONSTRUCT_TYPE_J)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId, BizProjectRecord::getTunnelId)
                .groupBy(BizProjectRecord::getTunnelId);
        List<Map<String,Object>> locationMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper);

        MPJLambdaWrapper<BizProjectRecord> queryWrapper1 = getWrapper(permission, dto );
        queryWrapper1
                .selectAs(BizWorkface::getWorkfaceName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .eq(BizProjectRecord::getConstructType,BizBaseConstant.CONSTRUCT_TYPE_H)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId, BizProjectRecord::getWorkfaceId)
                .groupBy(BizProjectRecord::getWorkfaceId);
        List<Map<String,Object>> huicaiMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper1);
        for (Map<String, Object> stringObjectMap : huicaiMap) {
            String  name = "回采"+stringObjectMap.get("name").toString();
            Object  value = stringObjectMap.get("value");
            stringObjectMap.put(name,value);
        }
        locationMap.addAll(huicaiMap);


        queryWrapper.clear();
        queryWrapper = getWrapper(permission, dto );
        queryWrapper
                .selectAs(SysDictData::getDictLabel,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizProjectRecord::getDrillType)
                .groupBy(BizProjectRecord::getDrillType);
        List<Map<String,Object>> drillTypeMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper);

        queryWrapper.clear();
        queryWrapper = new MPJLambdaWrapper<>();

        queryWrapper
                .selectAs(ConstructionUnitEntity::getConstructionUnitName,"name")
                .selectCount(BizProjectRecord::getProjectId,"value")
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .groupBy(BizProjectRecord::getConstructUnitId);
        List<Map<String,Object>> unitMap = bizProjectRecordMapper.selectJoinMaps(queryWrapper);

        BizProStatsVo vo = new BizProStatsVo();
        vo.setLocationMap(locationMap).setTypeMap(drillTypeMap).setUnitMap(unitMap);
        return vo;
    }


    private List<Long> getLocationByName(String locationName){
        List<Long> locationIds = new ArrayList<>();

        if(StrUtil.isNotEmpty(locationName)){
            QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
            tunnelQueryWrapper.lambda().select(TunnelEntity::getTunnelId).like(TunnelEntity::getTunnelName,locationName);
            List<TunnelEntity> tunnelList = tunnelMapper.selectList(tunnelQueryWrapper);
            if(tunnelList != null && tunnelList.size() > 0){
                locationIds = tunnelList.stream().map(TunnelEntity::getTunnelId).collect(Collectors.toList());
            }

            QueryWrapper<BizWorkface> workFaceQueryWrapper = new QueryWrapper<>();
            workFaceQueryWrapper.lambda().like(BizWorkface::getWorkfaceName,locationName);
            List<BizWorkface> workFaceList = bizWorkfaceMapper.selectList(workFaceQueryWrapper);
            if(workFaceList != null && workFaceList.size() > 0){
                locationIds.addAll(workFaceList.stream().map(BizWorkface::getWorkfaceId).collect(Collectors.toList()));
            }
        }
        return locationIds;
    }

    private  MPJLambdaWrapper<BizProjectRecord> getWrapper(BasePermission permission, BizWashProofDto dto ){

        List<Long> locationIds = getLocationByName(dto.getConstructLocation());


        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
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
                .eq(dto.getStatus() != null, BizProjectRecord::getStatus,dto.getStatus())
                .eq(dto.getLocationId() != null, BizProjectRecord::getLocationId,dto.getLocationId())
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(dto.getConstructUnitId() != null, BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getConstructShiftId() != null, BizProjectRecord::getConstructShiftId,dto.getConstructShiftId())
                .eq(StrUtil.isNotEmpty(dto.getDrillNum()), BizProjectRecord::getDrillNum,dto.getDrillNum())
                .eq(locationIds !=null && locationIds.size()>0, BizProjectRecord::getLocationId,locationIds)
                .between(startDate != null && currentDate != null,BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()));
        return queryWrapper;
    }







    @Override
    public MPage<BizProjectRecordPaibanVo> selectPaiList(BasePermission permission, BizCardVDto dto, Pagination pagination) {
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
                .select(BizDrillRecord::getRealDeep, BizDrillRecord::getDiameter, BizDrillRecord::getRemark)
                .innerJoin(BizProjectRecord.class,BizProjectRecord::getProjectId, BizDrillRecord::getProjectId)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getWorkfaceId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId)
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .leftJoin(ConstructionPersonnelEntity.class,ConstructionPersonnelEntity::getConstructionPersonnelId,BizProjectRecord::getWorker)
                .between(startDate != null && currentDate != null,BizProjectRecord::getConstructTime,startDate,currentDate)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_N);

        IPage<BizProjectRecordPaibanVo> maps = bizDrillRecordMapper.selectJoinPage(pagination,BizProjectRecordPaibanVo.class,queryWrapper);
        return new MPage<>(maps);
    }

    @Override
    public MPage<Map<String, Object>> monitorProject(BizPlanDto dto, Pagination pagination) {


        if(dto.getConstructType().equals(BizBaseConstant.CONSTRUCT_TYPE_H)){
            MPJLambdaWrapper<RelatesInfoEntity> queryWrapper = new MPJLambdaWrapper<>();
            queryWrapper.innerJoin(PlanEntity.class, PlanEntity::getPlanId,RelatesInfoEntity::getPlanId)
                    .leftJoin(BizDrillRecord.class,BizDrillRecord::getPlanId,RelatesInfoEntity::getPlanId)
                    .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,RelatesInfoEntity::getPositionId)
                    .eq(RelatesInfoEntity::getType,dto.getConstructType())
                    .selectAs(BizWorkface::getWorkfaceName,"positionName")
                    .selectSum(BizDrillRecord::getRealDeep,"realDeep")
                    .selectCount(BizDrillRecord::getDrillRecordId,"drillRealNum")
                    .selectAs(PlanEntity::getPlanName,"planName")
                    .selectAs(RelatesInfoEntity::getPlanType,"planType")
                    .selectAs(RelatesInfoEntity::getDrillNumber,"drillNumber")
                    .selectAs(RelatesInfoEntity::getHoleDepth,"holeDepth");
            IPage<Map<String,Object>> ps =  relatesInfoMapper.selectJoinMapsPage(pagination,queryWrapper);
            return new MPage<>(ps);
        }

        if(dto.getConstructType().equals(BizBaseConstant.CONSTRUCT_TYPE_J)){
            MPJLambdaWrapper<RelatesInfoEntity> queryWrapper = new MPJLambdaWrapper<>();
            queryWrapper.innerJoin(PlanEntity.class, PlanEntity::getPlanId,RelatesInfoEntity::getPlanId)
                    .leftJoin(BizDrillRecord.class,BizDrillRecord::getPlanId,RelatesInfoEntity::getPlanId)
                    .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,RelatesInfoEntity::getPositionId)
                    .eq(RelatesInfoEntity::getType,dto.getConstructType())
                    .selectAs(TunnelEntity::getTunnelName,"positionName")
                    .selectSum(BizDrillRecord::getRealDeep,"realDeep")
                    .selectCount(BizDrillRecord::getDrillRecordId,"drillRealNum")
                    .selectAs(PlanEntity::getPlanName,"planName")
                    .selectAs(RelatesInfoEntity::getPlanType,"planType")
                    .selectAs(RelatesInfoEntity::getDrillNumber,"drillNumber")
                    .selectAs(RelatesInfoEntity::getHoleDepth,"holeDepth");
            IPage<Map<String,Object>> ps =  relatesInfoMapper.selectJoinMapsPage(pagination,queryWrapper);
            return new MPage<>(ps);
        }
        return new MPage<>(null);
    }

    @Override
    public BizProjectRecordDetailVo selectById(Long bizProjectRecordId) {
        BizProjectRecord record =  this.getByIdDeep(bizProjectRecordId);
        if(record == null || record.getProjectId() == null){
            return null;
        }
        BizProjectRecordDetailVo vo = new BizProjectRecordDetailVo();
        BeanUtil.copyProperties(record,vo);

        QueryWrapper<BizDrillRecord> drillRecordQueryWrapper = new QueryWrapper<BizDrillRecord>();
        drillRecordQueryWrapper.lambda().eq(BizDrillRecord::getProjectId, record.getProjectId()).eq(BizDrillRecord::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<BizDrillRecord> drillRecordList =  bizDrillRecordMapper.selectList(drillRecordQueryWrapper);

        QueryWrapper<BizVideo> videoQueryWrapper = new QueryWrapper<BizVideo>();
        videoQueryWrapper.lambda().eq(BizVideo::getProjectId, record.getProjectId()).eq(BizVideo::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<BizVideo> videos =  bizVideoMapper.selectList(videoQueryWrapper);

        vo.setVideoList(videos).setDrillRecordList(drillRecordList);
        return  vo;
    }

    @Override
    public List<BizProjectRecordListVo> auditList(BizProjectRecord bizProjectRecord) {
        return null;
    }

    @Override
    public int saveRecord(BizProjectRecordAddDto dto) {
        BizTunnelBar bar = bizTunnelBarMapper.selectById(dto.getBarId());

        String detailJson = dto.getDrillRecords().get(0).getDetailJson();
//        dto.getRange();
//        dto.setConstructRange(dto.getRange());
        Integer barAngle = 0;
        if(detailJson != null && StrUtil.isNotBlank(detailJson) && !detailJson.equals("[]")){
            JSONArray array = JSONUtil.parseArray(detailJson);
            Integer bear_angle = JSONUtil.parseObj(array.get(0)).getInt("bear_angle");
            barAngle = bar.getDirectAngle() + 90 - bear_angle;
        }





        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        QueryWrapper<SysDept> deptQueryWrapper = new QueryWrapper<>();
        deptQueryWrapper.lambda().select(SysDept::getConstructionUnitId).eq(SysDept::getDeptId,currentUser.getDeptId());
        List<SysDept> sysDepts = sysDeptMapper.selectList(deptQueryWrapper);
        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setTag(ConstantsInfo.INITIAL_TAG);
        if(sysDepts != null && sysDepts.size() > 0){
            entity.setConstructionUnitId(sysDepts.get(0).getConstructionUnitId());
        }
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0).setDeptId(currentUser.getDeptId());
         this.getBaseMapper().insert(entity);



        try{
            BizPresetPoint point = bizTravePointService.getPointLatLon(dto.getTravePointId(),Double.parseDouble(dto.getConstructRange()));
            if(point != null && point.getPointId() != null){
                Long dangerAreaId = bizTravePointService.judgePointInArea(point.getPointId(),point.getMeter());
                Double ddd = dto.getDrillRecords().get(0).getRealDeep().multiply(new BigDecimal(bar.getDirectRange())).doubleValue();
                BizPresetPoint projectPoint = bizTravePointService.getLatLontop(point.getLatitude(),point.getLongitude(),ddd,barAngle);
                point.setLongitudet(projectPoint.getLongitudet())
                        .setLatitudet(projectPoint.getLatitudet())
                        .setDrillType(dto.getDrillType())
                        .setTunnelId(dto.getTunnelId())
                        .setWorkfaceId(dto.getWorkfaceId())
                        .setProjectId(entity.getProjectId())
                        .setDangerAreaId(dangerAreaId)
                        .setTunnelBarId(bar.getBarId());
                bizPresetPointMapper.insert(point);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }


        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            IntStream.range(0, dto.getDrillRecords().size()).forEach(i -> {
                BizDrillRecordDto drillRecordDto = dto.getDrillRecords().get(i);
                BizDrillRecord bizDrillRecord  = new BizDrillRecord();
                BeanUtil.copyProperties(drillRecordDto, bizDrillRecord);
                bizDrillRecord.setStatus(0).setTravePointId(dto.getTravePointId()).setProjectId(entity.getProjectId()).setNo(i + 1); // i + 1 表示当前是第几个 drillRecord
                bizDrillRecordMapper.insert(bizDrillRecord);
            });
        }

        if(dto.getVideos() != null && dto.getVideos().size() > 0){
            dto.getVideos().forEach(bizVideo -> {
                BizVideo video = new BizVideo();
                BeanUtil.copyProperties(bizVideo, video);
                video.setProjectId(entity.getProjectId());
                bizVideoMapper.insert(video);
            });
        }



        return 1;
    }

    @Override
    public int saveRecordApp(BizProjectRecordAddDto dto) {

//        dto.setConstructUnitId(dto.getConstructionUnitId());
        QueryWrapper<SysDept> deptQueryWrapper = new QueryWrapper<>();
        deptQueryWrapper.lambda().select(SysDept::getDeptId).eq(SysDept::getConstructionUnitId,dto.getConstructUnitId());
        List<SysDept> sysDepts = sysDeptMapper.selectList(deptQueryWrapper);


        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setPositionType("position_point");
        entity.setTag(ConstantsInfo.TEAM_TAG);
        entity.setConstructRange(dto.getAround()+dto.getRange());

        entity.setStatus(Integer.valueOf(ConstantsInfo.AUDIT_STATUS_DICT_VALUE));
        if(sysDepts != null && sysDepts.size() > 0){
            entity.setDeptId(sysDepts.get(0).getDeptId());
        }
        //掘进回采id
        if(dto.getConstructType().equals(BizBaseConstant.CONSTRUCT_TYPE_H)){
//            entity.setWorkfaceId(dto.getLocationId());
            entity.setLocationId(dto.getWorkfaceId());
        }else {
//            entity.setTunnelId(dto.getLocationId());
            entity.setLocationId(dto.getTunnelId());
        }
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);
//                .setDeptId(currentUser.getDeptId());
        this.getBaseMapper().insert(entity);

        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            IntStream.range(0, dto.getDrillRecords().size()).forEach(i -> {
                BizDrillRecordDto drillRecordDto = dto.getDrillRecords().get(i);
                BizDrillRecord bizDrillRecord  = new BizDrillRecord();
                BeanUtil.copyProperties(drillRecordDto, bizDrillRecord);
                bizDrillRecord.setStatus(0).setTravePointId(dto.getTravePointId()).setProjectId(entity.getProjectId()).setNo(i + 1); // i + 1 表示当前是第几个 drillRecord
                bizDrillRecordMapper.insert(bizDrillRecord);
            });
        }

        if(dto.getVideos() != null && dto.getVideos().size() > 0){
            dto.getVideos().forEach(bizVideo -> {
                BizVideo video = new BizVideo();
                BeanUtil.copyProperties(bizVideo, video);
                video.setProjectId(entity.getProjectId());
                bizVideoMapper.insert(video);
            });
        }

        return 1;    }

    @Override
    public int updateRecord(BizProjectRecordAddDto dto) {

        BizTunnelBar bar = bizTunnelBarMapper.selectById(dto.getBarId());

        String detailJson = dto.getDrillRecords().get(0).getDetailJson();
        Integer barAngle = 0;
        if(detailJson != null && StrUtil.isNotBlank(detailJson) && !detailJson.equals("[]")){
            JSONArray array = JSONUtil.parseArray(detailJson);
            Integer bear_angle = JSONUtil.parseObj(array.get(0)).getInt("bear_angle");
            barAngle = bar.getDirectAngle() + 90 - bear_angle;
        }

        BizPresetPoint point = bizTravePointService.getPointLatLon(dto.getTravePointId(),Double.parseDouble(dto.getConstructRange()));
        Long dangerAreaId = bizTravePointService.judgePointInArea(point.getPointId(),point.getMeter());
        if(point != null && point.getPointId() != null){
            BizPresetPoint projectPoint = bizTravePointService.getLatLontop(point.getLatitude(),point.getLatitude(),dto.getDrillRecords().get(0).getRealDeep().multiply(new BigDecimal(bar.getDirectAngle())).doubleValue(),barAngle);
            point.setLongitudet(projectPoint.getLongitudet())
                    .setLatitudet(projectPoint.getLatitudet())
                    .setDrillType(dto.getDrillType())
                    .setTunnelId(dto.getTunnelId())
                    .setDangerAreaId(dangerAreaId)
                    .setTunnelBarId(bar.getBarId());
            bizPresetPointMapper.insert(point);
        }


        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);

        UpdateWrapper<BizDrillRecord> drillUpdateWrapper= new UpdateWrapper<>();
        drillUpdateWrapper.lambda().eq(BizDrillRecord::getProjectId,dto.getProjectId()).set(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizDrillRecordMapper.update(null,drillUpdateWrapper);

        UpdateWrapper<BizVideo> videoUpdateWrapper= new UpdateWrapper<>();
        videoUpdateWrapper.lambda().eq(BizVideo::getProjectId,dto.getProjectId()).set(BizVideo::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizVideoMapper.update(null,videoUpdateWrapper);

        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            IntStream.range(0, dto.getDrillRecords().size()).forEach(i -> {
                BizDrillRecordDto drillRecordDto = dto.getDrillRecords().get(i);
                BizDrillRecord bizDrillRecord  = new BizDrillRecord();
                BeanUtil.copyProperties(drillRecordDto, bizDrillRecord);
                bizDrillRecord.setDrillRecordId(null);
                bizDrillRecord.setStatus(0).setTravePointId(dto.getTravePointId()).setProjectId(entity.getProjectId()).setNo(i + 1); // i + 1 表示当前是第几个 drillRecord
                bizDrillRecordMapper.insert(bizDrillRecord);
            });
        }
        if(dto.getVideos() != null && dto.getVideos().size() > 0){
            dto.getVideos().forEach(bizVideo -> {
                BizVideo video = new BizVideo();
                BeanUtil.copyProperties(bizVideo, video);
                video.setVideoId(null);
                video.setProjectId(dto.getProjectId());
                bizVideoMapper.insert(video);
            });
        }

        return 1;
    }


    @Override
    public int removeByProId(Long projectId) {

        UpdateWrapper<BizDrillRecord> drillUpdateWrapper= new UpdateWrapper<>();
        drillUpdateWrapper.lambda().eq(projectId != null, BizDrillRecord::getProjectId,projectId)
                .set(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizDrillRecordMapper.update(null,drillUpdateWrapper);

        UpdateWrapper<BizVideo> videoUpdateWrapper= new UpdateWrapper<>();
        videoUpdateWrapper.lambda().eq(projectId != null, BizVideo::getProjectId,projectId)
                .set(BizVideo::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizVideoMapper.update(null,videoUpdateWrapper);

        this.removeById(projectId);
        return 1;
    }

    @Override
    public int removeByProIds(Long[] projectIds) {

        UpdateWrapper<BizDrillRecord> drillUpdateWrapper= new UpdateWrapper<>();
        drillUpdateWrapper.lambda().in(projectIds.length > 0, BizDrillRecord::getProjectId,projectIds)
                .set(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizDrillRecordMapper.update(null,drillUpdateWrapper);

        UpdateWrapper<BizVideo> videoUpdateWrapper= new UpdateWrapper<>();
        videoUpdateWrapper.lambda().in(projectIds.length > 0, BizVideo::getProjectId,projectIds)
                .set(BizVideo::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizVideoMapper.update(null,videoUpdateWrapper);

        this.removeByIds(Arrays.asList(projectIds));
        return 1;
    }


    @Override
    public int firstAudit(BizProjectRecordDto dto) {
//        BizProjectAudit audit = new BizProjectAudit();
//        audit.setProjectId(dto.getProjectId())
//                .setMsg(dto.getMsg())
//                .setLevel("TEAM")
//                .setStatus(dto.getAudit());
//        bizProjectAuditMapper.insert(audit);
//        BizProjectRecord entity = new BizProjectRecord();
//        entity.setProjectId(dto.getProjectId())
//                        .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_TEAM_PASS:BizBaseConstant.FILL_STATUS_TEAM_BACK);
//        bizProjectRecordMapper.updateById(entity);
        return 1;
    }


    @Override
    public int secondAudit(BizProjectRecordDto dto) {
//        BizProjectAudit audit = new BizProjectAudit();
//        audit.setProjectId(dto.getProjectId())
//                .setMsg(dto.getMsg())
//                .setLevel("DEPT")
//                .setStatus(dto.getAudit());
//        bizProjectAuditMapper.insert(audit);
//        BizProjectRecord entity = new BizProjectRecord();
//        entity.setProjectId(dto.getProjectId())
//                .setStatus(dto.getStatus() == 1 ? BizBaseConstant.FILL_STATUS_DEPART_PASS:BizBaseConstant.FILL_STATUS_DEPART_BACK);
//        bizProjectRecordMapper.updateById(entity);
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
                .select(BizDrillRecord::getDirection, BizDrillRecord::getRealDeep)
                .select(BizProjectRecord::getConstructUnitId,BizProjectRecord::getTunnelId,BizProjectRecord::getRemark)
                .innerJoin(BizDrillRecord.class, BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(ConstructionUnitEntity.class,ConstructionUnitEntity::getConstructionUnitId,BizProjectRecord::getConstructUnitId)
                .innerJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getTunnelId);
//                .eq(BizDrillRecord::getDirection,direction)
//                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_LDPR)
//                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N);
        return projectQueryWrapper;

    }

    private List<Double> addData(BizProjectCDMAP map){

        List<Double> values = new ArrayList<>();
        List<BizProJson> jsons =  JSONUtil.toList(map.getCrumbWeight(),BizProJson.class);
        if (jsons != null && jsons.size() > 0){
            for (BizProJson json : jsons){
                values.add(json.getValue());
            }
        }
        return values;
    }

    @Override
    public void get444(BizProjectRecordDto1 dto,HttpServletResponse response) throws IOException {
        AtomicReference<XSSFWorkbook> wb = new AtomicReference<>();
        //查询工作面 下的 巷道
        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda()
                .select(TunnelEntity::getTunnelId)
                .eq(TunnelEntity::getWorkFaceId,dto.getWorkfaceId())
                .eq(TunnelEntity::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<TunnelEntity> tunnelList = tunnelMapper.selectList(tunnelQueryWrapper);
        List<Long> tunnelIds = new ArrayList<>();
//        Assert.isTrue(tunnelList != null && tunnelList.size() > 0, "没有巷道");
        tunnelIds = tunnelList.stream().map(TunnelEntity::getTunnelId).collect(Collectors.toList());

        //todo 预警值还没有表 设为
        List<String>  alarmLabels = Arrays.asList("1m","2m", "3m", "4m", "5m", "6m", "7m", "8m", "9m","10m","11m","12m","13m","14m");
        List<Double>  alarmValues = Arrays.asList(3.1, 3.1, 3.9, 3.1, 3.4, 3.1, 3.2, 3.1, 3.1,3.5,3.1,3.1,3.8,3.1);

        MPJLambdaWrapper<BizProjectRecord> projectRecordQueryWrapper = new MPJLambdaWrapper<>();

        projectRecordQueryWrapper
                .selectFunc(() -> "DATE_FORMAT(%s, '%%Y-%%m-%%d %%H:%%i:%%s')", BizProjectRecord::getConstructTime)
                .select(BizDrillRecord::getCrumbWeight)
                .selectAs(TunnelEntity::getTunnelName,BizProjectRecord::getTunnelName)
                .select(BizProjectRecord::getConstructRange,BizProjectRecord::getTunnelId,BizProjectRecord::getTravePointId)
                .in(BizProjectRecord::getTunnelId,tunnelIds)
                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_CD)
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(BizProjectRecord::getConstructType,BizBaseConstant.CONSTRUCT_TYPE_J)
                .innerJoin(BizDrillRecord.class, BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId);
        List<BizProjectCDMAP> cdmaps = bizProjectRecordMapper.selectJoinList(BizProjectCDMAP.class,projectRecordQueryWrapper);
        Assert.isTrue(cdmaps != null && cdmaps.size() > 0, "没有记录");

        //先根据 巷道分组
        final Map<Long, List<BizProjectCDMAP>>[] groupedByTunnelId = new Map[]{cdmaps.stream()
                .collect(Collectors.groupingBy(BizProjectCDMAP::getTunnelId))};

        groupedByTunnelId[0].forEach((tunnelId, groupbytunnel) -> {
            System.out.println("TunnelId: " + tunnelId);
            Map<String, List<BizProjectCDMAP>> groupbyTime = groupbytunnel
                    .stream().collect(Collectors.groupingBy(BizProjectCDMAP::getConstructTime));
            MPJLambdaWrapper<BizProjectRecord> rangePointMapper = new MPJLambdaWrapper<>();
            rangePointMapper.leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizProjectRecord::getTravePointId)
                    .select(BizProjectRecord::getConstructRange, BizProjectRecord::getTravePointId)
                    .selectAs(BizTravePoint::getPointName,BizProjectRecord::getPointName)
                    .eq(BizProjectRecord::getTunnelId, tunnelId)
                    .eq(BizProjectRecord::getDelFlag, BizBaseConstant.DELFLAG_N)
                    .eq(BizProjectRecord::getConstructType, BizBaseConstant.CONSTRUCT_TYPE_J)
                    .groupBy(BizProjectRecord::getTravePointId,BizProjectRecord::getConstructRange);
            //todo 需要的话 加日期筛选
            List<BizProjectRecord> rangePoints = bizProjectRecordMapper.selectList(rangePointMapper);
            List<ChartDataAll> chartDataAlls = new ArrayList<>();
            for (BizProjectRecord rangePoint : rangePoints) {
                ChartDataAll chartDataAll = new ChartDataAll();
                List<ChartData> chartDataList = new ArrayList<>();
                chartDataAll.setTitle(groupbytunnel.get(0).getTunnelName()+rangePoint.getPointName()+rangePoint.getConstructRange());
                chartDataList.add(setAlarm(alarmValues));
                groupbyTime.forEach((dateTime, group) -> {
                    List<BizProjectCDMAP> filteredRecords = group.stream().filter(record -> rangePoint.getConstructRange().equals(record.getConstructRange()) && record.getTravePointId() == rangePoint.getTravePointId()).collect(Collectors.toList());
                    ChartData chartData = new ChartData();
                    chartData.setTitle(dateTime);
                    if(filteredRecords != null && filteredRecords.size() > 0){
                        chartData.setData(addData( filteredRecords.get(0)));
                        chartDataList.add(chartData);
                        //默认只有一个同一时间点 同一导线点 同一距离 只有一个数据
                    }
                });
                chartDataAll.setChartDataList(chartDataList);
                chartDataAlls.add(chartDataAll);
            }

            try {
                wb.set(apachePoiLineChart.sssssss(wb.get(), groupbytunnel.get(0).getTunnelName()+"掘进", alarmLabels, chartDataAlls));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }



        });

        MPJLambdaWrapper<BizProjectRecord> projectRecordQueryWrapper1 = new MPJLambdaWrapper<>();

        projectRecordQueryWrapper1
                .selectFunc(() -> "DATE_FORMAT(%s, '%%Y-%%m-%%d %%H:%%i:%%s')", BizProjectRecord::getConstructTime)
                .select(BizDrillRecord::getCrumbWeight)
                .selectAs(TunnelEntity::getTunnelName,BizProjectRecord::getTunnelName)
                .select(BizProjectRecord::getConstructRange,BizProjectRecord::getTunnelId,BizProjectRecord::getTravePointId)
                .in(BizProjectRecord::getTunnelId,tunnelIds)
                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_CD)
                .eq(BizProjectRecord::getDelFlag,BizBaseConstant.DELFLAG_N)
                .eq(BizProjectRecord::getConstructType,BizBaseConstant.CONSTRUCT_TYPE_H)
                .innerJoin(BizDrillRecord.class, BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId);
        List<BizProjectCDMAP> cdmaps1 = bizProjectRecordMapper.selectJoinList(BizProjectCDMAP.class,projectRecordQueryWrapper1);
        Assert.isTrue(cdmaps != null && cdmaps.size() > 0, "没有记录");

        //先根据 巷道分组
        final Map<Long, List<BizProjectCDMAP>>[] groupedByTunnelId1 = new Map[]{cdmaps1.stream()
                .collect(Collectors.groupingBy(BizProjectCDMAP::getTunnelId))};

        groupedByTunnelId1[0].forEach((tunnelId, groupbytunnel) -> {
            System.out.println("TunnelId: " + tunnelId);
            Map<String, List<BizProjectCDMAP>> groupbyTime = groupbytunnel
                    .stream().collect(Collectors.groupingBy(BizProjectCDMAP::getConstructTime));
            MPJLambdaWrapper<BizProjectRecord> rangePointMapper = new MPJLambdaWrapper<>();
            rangePointMapper.leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizProjectRecord::getTravePointId)
                    .select(BizProjectRecord::getConstructRange, BizProjectRecord::getTravePointId)
                    .selectAs(BizTravePoint::getPointName,BizProjectRecord::getPointName)
                    .eq(BizProjectRecord::getTunnelId, tunnelId)
                    .eq(BizProjectRecord::getDelFlag, BizBaseConstant.DELFLAG_N)
                    .eq(BizProjectRecord::getConstructType, BizBaseConstant.CONSTRUCT_TYPE_H)
                    .groupBy(BizProjectRecord::getTravePointId,BizProjectRecord::getConstructRange);
            //todo 需要的话 加日期筛选
            List<BizProjectRecord> rangePoints = bizProjectRecordMapper.selectList(rangePointMapper);
            List<ChartDataAll> chartDataAlls = new ArrayList<>();
            for (BizProjectRecord rangePoint : rangePoints) {
                ChartDataAll chartDataAll = new ChartDataAll();
                List<ChartData> chartDataList = new ArrayList<>();
                chartDataAll.setTitle(groupbytunnel.get(0).getTunnelName()+rangePoint.getPointName()+rangePoint.getConstructRange());
                chartDataList.add(setAlarm(alarmValues));
                groupbyTime.forEach((dateTime, group) -> {
                    List<BizProjectCDMAP> filteredRecords = group.stream().filter(record -> rangePoint.getConstructRange().equals(record.getConstructRange()) && record.getTravePointId() == rangePoint.getTravePointId()).collect(Collectors.toList());
                    ChartData chartData = new ChartData();
                    chartData.setTitle(dateTime);
                    if(filteredRecords != null && filteredRecords.size() > 0){
                        chartData.setData(addData( filteredRecords.get(0)));
                        chartDataList.add(chartData);
                        //默认只有一个同一时间点 同一导线点 同一距离 只有一个数据
                    }
                });
                chartDataAll.setChartDataList(chartDataList);
                chartDataAlls.add(chartDataAll);
            }

            try {
                wb.set(apachePoiLineChart.sssssss(wb.get(), groupbytunnel.get(0).getTunnelName()+"回采", alarmLabels, chartDataAlls));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }



        });

        // 生成 Excel 并写入响应流

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=example.xlsx");
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            wb.get().write(byteArrayOutputStream);
            byte[] excelData = byteArrayOutputStream.toByteArray();
            response.setContentLength(excelData.length);
            response.getOutputStream().write(excelData);
            wb.get().close();
        }
    }


    @Override
    public void get999(String startDate, String endDate, Long tunnelId, Long workfaceId, String constructType, HttpServletResponse response) throws IOException {
        MPJLambdaWrapper<BizProjectRecord> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                .selectAs(BizProjectRecord::getDrillType,BizPulverizedCoalDailyVo::getDrillType)
                .selectAs(BizProjectRecord::getConstructRange,BizPulverizedCoalDailyVo::getConstructRange)
                .selectAs(BizProjectRecord::getConstructType,BizPulverizedCoalDailyVo::getConstructType)
                .selectAs(BizProjectRecord::getConstructTime,BizPulverizedCoalDailyVo::getConstructTime)
                .selectAs(BizProjectRecord::getWorkfaceId,BizPulverizedCoalDailyVo::getWorkfaceId)
                .selectAs(BizProjectRecord::getTunnelId,BizPulverizedCoalDailyVo::getTunnelId)
                .selectAs(BizProjectRecord::getBarId,BizPulverizedCoalDailyVo::getBarId)
                .selectAs(BizProjectRecord::getTravePointId,BizPulverizedCoalDailyVo::getTravePointId)

                .selectAs(BizWorkface::getWorkfaceName,BizPulverizedCoalDailyVo::getWorkfaceName)
                .selectAs(TunnelEntity::getTunnelName,BizPulverizedCoalDailyVo::getTunnelName)
                .selectAs(BizTravePoint::getPointName,BizPulverizedCoalDailyVo::getPointName)
                .selectCollection(BizDrillRecord.class,BizPulverizedCoalDailyVo::getDrillRecords)

                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizProjectRecord::getProjectId)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizProjectRecord::getWorkfaceId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId)
                .leftJoin(BizDrillRecord.class,BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .eq(workfaceId != null ,BizProjectRecord::getWorkfaceId, workfaceId)
                .eq(tunnelId != null , BizProjectRecord::getTunnelId,tunnelId)
                .eq(StrUtil.isNotEmpty(constructType),BizProjectRecord::getConstructType,constructType)
                .eq(BizProjectRecord::getDrillType,BizBaseConstant.FILL_TYPE_CD)
                .between(StrUtil.isNotEmpty(startDate) && StrUtil.isNotEmpty(endDate),BizProjectRecord::getConstructTime,startDate,endDate);

        List<BizPulverizedCoalDailyVo> vos =  bizProjectRecordMapper.selectJoinList(BizPulverizedCoalDailyVo.class,mpjLambdaWrapper);

        final Map<Long, List<BizPulverizedCoalDailyVo>>[] groupedByTunnelId = new Map[]{vos.stream()
                .collect(Collectors.groupingBy(BizPulverizedCoalDailyVo::getTunnelId))};

        groupedByTunnelId[0].forEach((tunnId, groupbytunnel) -> {
            List<List<BizPulverizedCoalDailyDetailVo>> tunnelList = new ArrayList<>();
            final Map<Long, List<BizPulverizedCoalDailyVo>>[] groupedByPointId = new Map[]{groupbytunnel.stream()
                    .collect(Collectors.groupingBy(BizPulverizedCoalDailyVo::getTravePointId))};
            groupedByPointId[0].forEach((pointId, groupedbypoint) -> {
                tunnelList.add(sssssgeeessss(pointId,groupedbypoint));
            });
            System.out.println("tunnelId = " + tunnelList);
        });
    }

    public void sssssgeee(Long tunnelId, List<BizPulverizedCoalDailyVo> tunnelList) {


    }

    //获取 该导线点的 list
    public List<BizPulverizedCoalDailyDetailVo> sssssgeeessss(Long pointId, List<BizPulverizedCoalDailyVo> poinyList) {
        List<BizPulverizedCoalDailyDetailVo> point = new ArrayList<>();
        for (BizPulverizedCoalDailyVo bizPulverizedCoalDailyVo : poinyList) {
            BizPulverizedCoalDailyDetailVo vo = new BizPulverizedCoalDailyDetailVo();
            vo.setRiqi(bizPulverizedCoalDailyVo.getConstructTime()+"")
                    .setWeizhi(bizPulverizedCoalDailyVo.getConstructRange())
                    .setCrumWeight(bizPulverizedCoalDailyVo.getDrillRecords().get(0).getCrumbWeight());
            point.add(vo);
        }
        return point;
    }

    @Override
    public void sss555(HttpServletResponse response) {

    }

    @Override
    public void deletePlan(Long planId) {
        UpdateWrapper<PlanEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(PlanEntity::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(PlanEntity::getPlanId, planId);
        planMapper.delete(updateWrapper);
    }

    @Override
    public String submitForReview(Long projectId) {
        String flag = "";
        if (ObjectUtil.isNull(projectId)) {
            throw new RuntimeException("参数错误,工程id不能为空！");
        }
        BizProjectRecord bizProjectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, projectId)
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizProjectRecord)) {
            throw new RuntimeException("未找到此工程填报信息,无法进行审核");
        }
        BizProjectRecord bizProject = new BizProjectRecord();
        BeanUtils.copyProperties(bizProjectRecord, bizProject);
        bizProject.setStatus(Integer.valueOf(ConstantsInfo.AUDIT_STATUS_DICT_VALUE));
        bizProject.setTag(ConstantsInfo.TEAM_TAG);
        flag = bizProjectRecordMapper.updateById(bizProject) > 0 ? "提交审核成功" : "提交审核失败,请联系管理员";
        return flag;
    }

    @Override
    public String withdraw(Long projectId) {
        String flag = "";
        if (ObjectUtil.isNull(projectId)) {
            throw new RuntimeException("参数错误, 工程id不能为空！");
        }
        BizProjectRecord bizProjectRecord = bizProjectRecordMapper.selectOne(new LambdaQueryWrapper<BizProjectRecord>()
                .eq(BizProjectRecord::getProjectId, projectId)
                .eq(BizProjectRecord::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(bizProjectRecord)) {
            throw new RuntimeException("未找到此工程填报信息,无法进行审核");
        }
        checkStatus(String.valueOf(bizProjectRecord.getStatus()));
        BizProjectRecord bizProject = new BizProjectRecord();
        BeanUtils.copyProperties(bizProjectRecord, bizProject);
        bizProject.setStatus(Integer.valueOf(ConstantsInfo.TO_BE_SUBMITTED));
        bizProject.setTag(ConstantsInfo.INITIAL_TAG);
        flag = bizProjectRecordMapper.updateById(bizProject) > 0 ? "撤回成功" : "撤回失败,请联系管理员";
        return flag;
    }

    private ChartData setAlarm(List<Double> alarms){
        ChartData chartData12 = new ChartData();
        chartData12.setData(alarms); chartData12.setTitle("预警值");
        return chartData12;
    }

    /**
     * 校验状态
     * @param status 状态
     */
    private void checkStatus(String status) {
        if (status.equals(ConstantsInfo.TO_BE_SUBMITTED)) {
            throw new RuntimeException("此计划还未提交审核,无法撤回");
        }
        if (status.equals(ConstantsInfo.IN_REVIEW_DICT_VALUE)) {
            throw new RuntimeException("此计划正在审核中,无法撤回");
        }
        if (status.equals(ConstantsInfo.AUDITED_DICT_VALUE)) {
            throw new RuntimeException("此计划已审核通过,无法撤回");
        }
        if (status.equals(ConstantsInfo.REJECTED)) {
            throw new RuntimeException("此计划已驳回,无法撤回");
        }
    }
}












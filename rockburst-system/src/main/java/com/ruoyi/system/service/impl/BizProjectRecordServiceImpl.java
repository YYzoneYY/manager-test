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
import com.ruoyi.system.constant.MapConfigConstant;
import com.ruoyi.system.constant.ModelFlaskConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.dto.project.BizCardVDto;
import com.ruoyi.system.domain.dto.project.BizWashProofDto;
import com.ruoyi.system.domain.excel.BizProJson;
import com.ruoyi.system.domain.excel.ChartData;
import com.ruoyi.system.domain.excel.ChartDataAll;
import com.ruoyi.system.domain.utils.ClosestPointOnLine;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizProjectRecordService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.PlanService;
import com.ruoyi.system.service.impl.handle.AiModelHandle;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    @Autowired
    private BizPlanPresetMapper bizPlanPresetMapper;
    @Resource
    private DepartmentAuditMapper departmentAuditMapper;
    @Resource
    private TeamAuditMapper teamAuditMapper;

    @Autowired
    private AiModelHandle aiModelHandle;

    @Autowired
    private ISysConfigService configService;


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
                .eq(dto.getTunnelId() !=null,BizProjectRecord::getTunnelId,dto.getTunnelId())
                .eq(dto.getWorkfaceId() !=null,BizProjectRecord::getWorkfaceId,dto.getWorkfaceId())
                .eq(dto.getConstructType() !=null,BizProjectRecord::getConstructType,dto.getConstructType())
                .eq(dto.getConstructShiftId()!=null,BizProjectRecord::getConstructShiftId,dto.getConstructShiftId())
                .between(StrUtil.isNotEmpty(dto.getStartTime()),BizProjectRecord::getConstructTime,DateUtils.parseDate(dto.getStartTime()),DateUtils.parseDate(dto.getEndTime()))
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus())
                .orderByDesc(BizProjectRecord::getConstructTime);
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination , queryWrapper);
        List<BizProjectRecordListVo> vos =  sss.getRecords();
        List<BizProjectRecordListVo> vv = new ArrayList<>();
        vv = BeanUtil.copyToList(vos,BizProjectRecordListVo.class);
        sss.setRecords(vv);
        return new MPage<>(sss);
    }

    @Override
    public MPage<BizProjectRecordListVo> getlistAdudit(BasePermission permission,String fillingType, Long constructionUnitId,Integer[] status, Pagination pagination) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecordListVo.class)
                .selectAs(BizProjectRecord::getStatus, BizProjectRecordListVo::getConstructLocation)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(constructionUnitId!=null,BizProjectRecord::getConstructUnitId,constructionUnitId)
                .eq(fillingType !=null,BizProjectRecord::getDrillType,fillingType)
                .in(status !=null,BizProjectRecord::getStatus,status)
                .orderByDesc(BizProjectRecord::getConstructTime);
        IPage<BizProjectRecordListVo> sss = this.pageDeep(pagination , queryWrapper);
        return new MPage<>(sss);
    }


    @Override
    public MPage<BizProjectRecordDetailVo> pageAudit(BasePermission permission, BizProjectRecordDto dto, Integer[] statuss, Pagination pagination) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        MPJLambdaWrapper<BizProjectRecord> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizProjectRecordDetailVo.class)
                .selectAs(BizProjectRecord::getStatus, BizProjectRecordDetailVo::getConstructLocation)
                .and(permission.getDeptIds() != null && permission.getDeptIds().size()>0 ,i->i.in(permission.getDeptIds() != null && permission.getDeptIds().size()>0 , BizProjectRecord::getDeptId,permission.getDeptIds())
                        .or().eq(permission.getDateScopeSelf() == 5,BizProjectRecord::getCreateBy,currentUser.getUserName()))
                .eq(dto.getConstructUnitId()!=null,BizProjectRecord::getConstructUnitId,dto.getConstructUnitId())
                .eq(dto.getTunnelId() != null,BizProjectRecord::getTunnelId,dto.getTunnelId())
                .eq(dto.getWorkfaceId() != null,BizProjectRecord::getTunnelId,dto.getWorkfaceId())
                .eq(dto.getDrillType() !=null,BizProjectRecord::getDrillType,dto.getDrillType())
                .eq(dto.getConstructType() !=null,BizProjectRecord::getConstructType,dto.getConstructType())
                .eq(dto.getConstructShiftId()!=null,BizProjectRecord::getConstructShiftId,dto.getConstructShiftId())
                .between(StrUtil.isNotEmpty(dto.getStartTime()),BizProjectRecord::getConstructTime,DateUtils.parseDate(dto.getStartTime()),DateUtils.parseDate(dto.getEndTime()))
                .eq(dto.getStatus()!=null,BizProjectRecord::getStatus,dto.getStatus())
                .in(statuss !=null,BizProjectRecord::getStatus,statuss)
                .orderByDesc(BizProjectRecord::getConstructTime);
        IPage<BizProjectRecordDetailVo> sss = this.pageDeep(pagination , queryWrapper);
        List<BizProjectRecordDetailVo> vos =  sss.getRecords();

        List<BizProjectRecordDetailVo> vv = new ArrayList<>();
        vv = BeanUtil.copyToList(vos,BizProjectRecordDetailVo.class);

        for (BizProjectRecordDetailVo vo : vv) {
            QueryWrapper<BizDrillRecord> drillRecordQueryWrapper = new QueryWrapper<>();
            drillRecordQueryWrapper.lambda().eq(BizDrillRecord::getProjectId,vo.getProjectId());
            List<BizDrillRecord> drillRecords = bizDrillRecordMapper.selectList(drillRecordQueryWrapper);
            vo.setDrillRecordList(drillRecords);

            QueryWrapper<BizVideo> videoQueryWrapper = new QueryWrapper<>();
            videoQueryWrapper.lambda().eq(BizVideo::getProjectId,vo.getProjectId());
            List<BizVideo> videos = bizVideoMapper.selectList(videoQueryWrapper);
            vo.setVideoList(videos);
        }
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



    public int getAngle(Integer barAngle, Integer bearAngle){
        return barAngle + 90 - bearAngle;
    }

    public int getAngleYt(Integer barAngle){
        return barAngle + 180;
    }


    @Override
    public Long saveRecord(BizProjectRecordAddDto dto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();

        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setTag(ConstantsInfo.INITIAL_TAG);

        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0).setDeptId(currentUser.getDeptId());
        if(entity.getConstructType().equals(BizBaseConstant.CONSTRUCT_TYPE_J)){
            entity.setLocationId(entity.getTunnelId());
        }
        if(entity.getConstructType().equals(BizBaseConstant.CONSTRUCT_TYPE_H)){
            entity.setLocationId(entity.getWorkfaceId());
        }
        this.getBaseMapper().insert(entity);

        if(StrUtil.isNotEmpty(dto.getBarRange()) ){
            insertYtPoint(dto,entity.getProjectId());
        }else{
            insertPresetPoint(dto,entity.getProjectId());
        }

        insterDrills(dto,entity.getProjectId());

        insertVideos(dto,entity.getProjectId());

        return entity.getProjectId();
    }

    public int insertVideos(BizProjectRecordAddDto dto,Long projectId) {
        if(dto.getVideoList() != null && dto.getVideoList().size() > 0){
            dto.getVideoList().forEach(bizVideo -> {
                BizVideo video = new BizVideo();
                BeanUtil.copyProperties(bizVideo, video);
                video.setProjectId(projectId);
                video.setStatus(ModelFlaskConstant.ai_model_pending);
                bizVideoMapper.insert(video);

                // 事务提交后执行异步任务
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        CompletableFuture.runAsync(() -> {
                            try {
                                log.info("启动AI分析: {}", bizVideo.getFileName());
                                aiModelHandle.modelAnalyByFileId(
                                        video.getVideoId(), // 使用已回填的ID
                                        bizVideo.getBucket(),
                                        bizVideo.getFileUrl(),
                                        bizVideo.getFileName()
                                );
                            } catch (IOException e) {
                                log.error("AI分析失败: {}", bizVideo.getFileName(), e);
                            }
                        });
                    }
                });
            });
        }
        return 1;
    }


//    public int insertVideos(BizProjectRecordAddDto dto,Long projectId) {
//        if(dto.getVideoList() != null && dto.getVideoList().size() > 0){
//            dto.getVideoList().forEach(bizVideo -> {
//                BizVideo video = new BizVideo();
//                BeanUtil.copyProperties(bizVideo, video);
//                video.setProjectId(projectId);
//                video.setStatus(ModelFlaskConstant.ai_model_pending);
//                bizVideoMapper.insert(video);
//
//                CompletableFuture.supplyAsync(()->{
//                    try {
//                        BizVideo bbb  = bizVideoMapper.selectById(video.getVideoId());
//                        log.info("异步调用ai视频分析,视频名称:{}",bizVideo.getFileName());
//                        String taskId = aiModelHandle.modelAnalyByFileId(video.getVideoId(),bizVideo.getBucket(),bizVideo.getFileUrl(),bizVideo.getFileName());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    return 1;
//                });
//            });
//        }
//        return 1;
//    }

    private int insterDrills(BizProjectRecordAddDto dto,Long projectId){
        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            IntStream.range(0, dto.getDrillRecords().size()).forEach(i -> {
                BizDrillRecordDto drillRecordDto = dto.getDrillRecords().get(i);
                BizDrillRecord bizDrillRecord  = new BizDrillRecord();
                BeanUtil.copyProperties(drillRecordDto, bizDrillRecord);
                bizDrillRecord.setStatus(0).setTravePointId(dto.getTravePointId()).setProjectId(projectId).setNo(i + 1); // i + 1 表示当前是第几个 drillRecord
                bizDrillRecordMapper.insert(bizDrillRecord);
            });
        }
        return 1;
    }

    private BizPresetPoint getNearMinToBar(String lat,String lon,Long barId){
        BizTunnelBar bar = bizTunnelBarMapper.selectById(barId);
        BigDecimal[] bigDecimals = ClosestPointOnLine.getClosestPoint(new BigDecimal(bar.getA()),new BigDecimal(bar.getB()),new BigDecimal(bar.getC()),new BigDecimal(lat) ,new BigDecimal(lon));
        BizPresetPoint point = new BizPresetPoint();
        point.setAxisx(bigDecimals[0].toString());
        point.setAxisy(bigDecimals[1].toString());
        return point;
    }
    private String getJsonStr(BigDecimal[] bigDecimals,BigDecimal[] otherBarbigDecimals){
        List<Map<String,Object>> list = new ArrayList<>();
        String lat = bigDecimals[0].toString();
        String lon = bigDecimals[1].toString();
        String lat1 = otherBarbigDecimals[0].toString();
        String lon1 = otherBarbigDecimals[1].toString();
        Map<String, Object> map = new HashMap<>();
        map.put("lat", lat);
        map.put("lng", lon);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("lat", lat1);
        map1.put("lng", lon1);
        list.add(map);
        list.add(map1);
        return JSONUtil.toJsonStr(list);
    }





    private void insertYtPoint(BizProjectRecordAddDto dto,Long projectId){
        try{
//            BizPresetPoint point = bizTravePointService.getPointLatLon(dto.getTravePointId(),Double.parseDouble(dto.getConstructRange()));
            BizTravePoint point = bizTravePointService.getById(dto.getTravePointId());
            BizTunnelBar bar = bizTunnelBarMapper.selectById(dto.getBarId());
            //获取帮上的点
            //坐标获取 帮上的对应点   获取比例  根据走向 获取 孔在 帮上的坐标
            BigDecimal[] bigDecimals = getClosestPointOnSegment(point.getAxisx(),point.getAxisy(),bar.getStartx(), bar.getStarty(), bar.getEndx(), bar.getEndy());
            //

            String uploadUrl = configService.selectConfigByKey(MapConfigConstant.map_bili);

            BigDecimal[] pointssa = getExtendedPoint(bigDecimals[0].toString(), bigDecimals[1].toString(), bar.getDirectAngle()+180, Double.parseDouble(dto.getBarRange()), Double.parseDouble(uploadUrl));


            BigDecimal[] pointssasss = getExtendedPoint(pointssa[0].toString(), pointssa[1].toString(), bar.getTowardAngle(), Double.parseDouble(dto.getConstructRange()), Double.parseDouble(uploadUrl));

            BigDecimal[] pointssass = getExtendedPoint(pointssasss[0].toString(), pointssasss[1].toString(), bar.getYtAngle(), dto.getDrillRecords().get(0).getRealDeep().doubleValue(), Double.parseDouble(uploadUrl));


            List<Map<String,Object>> list = new ArrayList<>();
            Map<String,Object> map = new HashMap<>();
            map.put("x", pointssasss[0]);
            map.put("y", pointssasss[1]);


            Map<String,Object> map1 = new HashMap<>();
            map1.put("x", pointssass[0]);
            map1.put("y", pointssass[1]);


            list.add(map);
            list.add(map1);
            //新版本,曲折一一对应
            if(point != null && point.getPointId() != null){
                Long dangerAreaId = bizTravePointService.judgeXYInArea(bigDecimals[0].toString(),bigDecimals[1].toString(),dto.getTunnelId());

                BizPresetPoint presetPoint = new BizPresetPoint();
                String str = JSONUtil.parseArray(list).toString();
                presetPoint.setAxiss(str);
                presetPoint.setConstructTime(dto.getConstructTime())
                        .setDrillType(dto.getDrillType())
                        .setTunnelId(dto.getTunnelId())
                        .setWorkfaceId(dto.getWorkfaceId())
                        .setProjectId(projectId)
                        .setDangerAreaId(dangerAreaId)
                        .setTunnelBarId(bar.getBarId());
                bizPresetPointMapper.insert(presetPoint);
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }

    }

    private void updateYtPoint(BizProjectRecordAddDto dto,Long projectId){
        UpdateWrapper<BizPresetPoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizPresetPoint::getProjectId,projectId)
                        .set(BizPresetPoint::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizPresetPointMapper.update(new BizPresetPoint(),updateWrapper);

        try{
//            BizPresetPoint point = bizTravePointService.getPointLatLon(dto.getTravePointId(),Double.parseDouble(dto.getConstructRange()));
            BizTravePoint point = bizTravePointService.getById(dto.getTravePointId());
            BizTunnelBar bar = bizTunnelBarMapper.selectById(dto.getBarId());
            //获取帮上的点
            //坐标获取 帮上的对应点   获取比例  根据走向 获取 孔在 帮上的坐标
            BigDecimal[] bigDecimals = getClosestPointOnSegment(point.getAxisx(),point.getAxisy(),bar.getStartx(), bar.getStarty(), bar.getEndx(), bar.getEndy());
            //

            String uploadUrl = configService.selectConfigByKey(MapConfigConstant.map_bili);

            BigDecimal[] pointssa = getExtendedPoint(bigDecimals[0].toString(), bigDecimals[1].toString(), bar.getDirectAngle()+180, Double.parseDouble(dto.getBarRange()), Double.parseDouble(uploadUrl));




            BigDecimal[] pointssasss = getExtendedPoint(pointssa[0].toString(), pointssa[1].toString(), bar.getTowardAngle(), Double.parseDouble(dto.getConstructRange()), Double.parseDouble(uploadUrl));


            BigDecimal[] pointssass = getExtendedPoint(pointssasss[0].toString(), pointssasss[1].toString(), bar.getYtAngle(),dto.getDrillRecords().get(0).getRealDeep().doubleValue(), Double.parseDouble(uploadUrl));


            List<Map<String,Object>> list = new ArrayList<>();
            Map<String,Object> map = new HashMap<>();
            map.put("x", pointssasss[0]);
            map.put("y", pointssasss[1]);


            Map<String,Object> map1 = new HashMap<>();
            map1.put("x", pointssass[0]);
            map1.put("y", pointssass[1]);


            list.add(map);
            list.add(map1);
            //新版本,曲折一一对应
            if(point != null && point.getPointId() != null){
                Long dangerAreaId = bizTravePointService.judgeXYInArea(bigDecimals[0].toString(),bigDecimals[1].toString(),dto.getTunnelId());

                BizPresetPoint presetPoint = new BizPresetPoint();
                String str = JSONUtil.parseArray(list).toString();
                presetPoint.setAxiss(str);
                presetPoint.setConstructTime(dto.getConstructTime())
                        .setDrillType(dto.getDrillType())
                        .setTunnelId(dto.getTunnelId())
                        .setWorkfaceId(dto.getWorkfaceId())
                        .setProjectId(projectId)
                        .setDangerAreaId(dangerAreaId)
                        .setTunnelBarId(bar.getBarId());
                bizPresetPointMapper.insert(presetPoint);
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }

    }


    private void insertPresetPoint(BizProjectRecordAddDto dto,Long projectId){
        try{

            BizPresetPoint presetPoint = new BizPresetPoint();
            //帮的线段 和 走向
            BizTunnelBar bar = bizTunnelBarMapper.selectById(dto.getBarId());
            //拿到当前到导线点和距离 DF09 -9 换算坐标
            BizTravePoint point = bizTravePointService.getById(dto.getTravePointId());
            //坐标获取 帮上的对应点   获取比例  根据走向 获取 孔在 帮上的坐标
            BigDecimal[] bigDecimals = getClosestPointOnSegment(point.getAxisx(),point.getAxisy(),bar.getStartx(), bar.getStarty(), bar.getEndx(), bar.getEndy());

            String uploadUrl = configService.selectConfigByKey(MapConfigConstant.map_bili);

            BigDecimal[] pointssa = getExtendedPoint(bigDecimals[0].toString(), bigDecimals[1].toString(), bar.getTowardAngle(), Double.parseDouble(dto.getConstructRange()), Double.parseDouble(uploadUrl));

            //根据 方位角 和 每米的 方位角 获取下一个点的坐标 ,
            //返回坐标组
            if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
                String details = dto.getDrillRecords().get(0).getDetailJson();
                if(details != null && StrUtil.isNotBlank(details) && !details.equals("[]")){
                    JSONArray array = JSONUtil.parseArray(details);
                    List<Map<String,Object>> list = new ArrayList<>();

                    String lat = pointssa[0].toString();
                    String lon = pointssa[1].toString();
//                    BigDecimal[] pppp =  getExtendedPoint(lat,lon,bar.getDirectAngle(),1, Double.parseDouble(uploadUrl));
                    BigDecimal[] pppp =  pointssa;
                    lat = pppp[0].toString();
                    lon = pppp[1].toString();
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("x", lat);
                    map1.put("y", lon);
                    list.add(map1);
                    for (Object o : array) {
                        Integer bear_angle = JSONUtil.parseObj(o).getInt("bear_angle");
                        bear_angle = getAngle(bar.getDirectAngle(),bear_angle);
                        BigDecimal[] pppps = getExtendedPoint(lat,lon,bear_angle,1,Double.parseDouble(uploadUrl));
                        lat = pppps[0].toString();
                        lon = pppps[1].toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("x", lat);
                        map.put("y", lon);
                        list.add(map);
                    }
                    String str = JSONUtil.parseArray(list).toString();
                    presetPoint.setAxiss(str);
                }
            }

            //新版本,曲折一一对应
            if(point != null && point.getPointId() != null){
                Long dangerAreaId = bizTravePointService.judgeXYInArea(bigDecimals[0].toString(),bigDecimals[1].toString(),dto.getTunnelId());
//                Double ddd = new BigDecimal(bar.getDirectRange()).doubleValue();

                presetPoint.setConstructTime(dto.getConstructTime())
                        .setDrillType(dto.getDrillType())
                        .setTunnelId(dto.getTunnelId())
                        .setWorkfaceId(dto.getWorkfaceId())
                        .setProjectId(projectId)
                        .setDangerAreaId(dangerAreaId)
                        .setTunnelBarId(bar.getBarId());
                bizPresetPointMapper.insert(presetPoint);
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }

    }


    /**
     * 根据角度与距离计算延伸后的点坐标
     *
     * @param xa 原点 x 坐标（字符串）
     * @param ya 原点 y 坐标（字符串）
     * @param angleDeg 与 Y 轴正方向的夹角（单位：度）
     * @param i 距离（单位：米，可为负）
     * @param scale 坐标单位与米的比例（如 1 米 = 100 坐标单位，则 scale=100）
     * @return 延伸后点的坐标（BigDecimal[2]）
     */
    public static BigDecimal[] getExtendedPoint(
            String xa, String ya,
            double angleDeg,
            double i,
            double scale
    ) {
        BigDecimal x = new BigDecimal(xa);
        BigDecimal y = new BigDecimal(ya);

        // 转换为与 X 轴正方向的夹角（Math 以 X 正方向为 0°）
        double angleFromXAxis = 90 - angleDeg;

        // 弧度制
        double radians = Math.toRadians(angleFromXAxis);

        // 米 → 坐标单位
        double distanceInCoord = i * scale;

        // 计算偏移量
        double dx = distanceInCoord * Math.cos(radians);
        double dy = distanceInCoord * Math.sin(radians);

        // 新坐标
        BigDecimal newX = x.add(BigDecimal.valueOf(dx)).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal newY = y.add(BigDecimal.valueOf(dy)).setScale(6, BigDecimal.ROUND_HALF_UP);

        return new BigDecimal[]{newX, newY};
    }

    /**
     * 计算点 A(x1, y1) 到线段 MN(xm, ym)-(xn, yn) 的最近点坐标
     * 返回值为 BigDecimal[2]，分别是最近点的 x 和 y
     */
    public  BigDecimal[] getClosestPointOnSegment(
            String x1Str, String y1Str,
            String xmStr, String ymStr,
            String xnStr, String ynStr
    ) {
        // 转为 BigDecimal
        BigDecimal x1 = new BigDecimal(x1Str);
        BigDecimal y1 = new BigDecimal(y1Str);
        BigDecimal xm = new BigDecimal(xmStr);
        BigDecimal ym = new BigDecimal(ymStr);
        BigDecimal xn = new BigDecimal(xnStr);
        BigDecimal yn = new BigDecimal(ynStr);

        // 向量 MA = A - M, 向量 MN = N - M
        BigDecimal dx = xn.subtract(xm);
        BigDecimal dy = yn.subtract(ym);

        BigDecimal lengthSquared = dx.multiply(dx).add(dy.multiply(dy)); // MN 向量长度平方

        if (lengthSquared.compareTo(BigDecimal.ZERO) == 0) {
            // M == N，退化为点
            return new BigDecimal[]{xm, ym};
        }

        // 计算投影比例 t = (MA·MN) / |MN|²
        BigDecimal mx = x1.subtract(xm);
        BigDecimal my = y1.subtract(ym);

        BigDecimal dot = mx.multiply(dx).add(my.multiply(dy)); // MA·MN
        BigDecimal t = dot.divide(lengthSquared, 10, BigDecimal.ROUND_HALF_UP); // 保留 10 位小数

        // 限制 t 在 [0, 1] 之间
        if (t.compareTo(BigDecimal.ZERO) < 0) {
            t = BigDecimal.ZERO;
        } else if (t.compareTo(BigDecimal.ONE) > 0) {
            t = BigDecimal.ONE;
        }

        // 最近点 P = M + t * (N - M)
        BigDecimal px = xm.add(t.multiply(dx));
        BigDecimal py = ym.add(t.multiply(dy));

        return new BigDecimal[]{px.setScale(6, BigDecimal.ROUND_HALF_UP), py.setScale(6, BigDecimal.ROUND_HALF_UP)};
    }

    @Override
    public int saveRecordApp(BizProjectRecordAddDto dto) {

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
            entity.setLocationId(dto.getWorkfaceId());
        }else {
            entity.setLocationId(dto.getTunnelId());
        }
        entity.setStatus(BizBaseConstant.FILL_STATUS_PEND).setIsRead(0);
        this.getBaseMapper().insert(entity);

        if(dto.getBarRange() != null){
            insertYtPoint(dto,entity.getProjectId());
        }else{
            insertPresetPoint(dto,entity.getProjectId());
        }

        insterDrills(dto,entity.getProjectId());

        insertVideos(dto,entity.getProjectId());

        return 1;
    }

    @Override
    public int updateRecord(BizProjectRecordAddDto dto) {

        BizProjectRecord entity = new BizProjectRecord();
        BeanUtil.copyProperties(dto, entity);
        this.updateById(entity);

        if(StrUtil.isNotEmpty(dto.getBarRange())){
            updateYtPoint(dto,entity.getProjectId());
        }else{
            updatePresetPoint(dto,dto.getProjectId());
        }


        updateVideos(dto);

        updateDrills(dto);

        return 1;
    }


    private int updateVideos(BizProjectRecordAddDto dto){
        UpdateWrapper<BizVideo> videoUpdateWrapper= new UpdateWrapper<>();
        videoUpdateWrapper.lambda().eq(BizVideo::getProjectId,dto.getProjectId()).set(BizVideo::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizVideoMapper.update(null,videoUpdateWrapper);

        if(dto.getVideoList() != null && dto.getVideoList().size() > 0){
            dto.getVideoList().forEach(bizVideo -> {
                BizVideo video = new BizVideo();
                BeanUtil.copyProperties(bizVideo, video);
                video.setVideoId(null);
                video.setProjectId(dto.getProjectId());
                bizVideoMapper.insert(video);
            });
        }
        return 1;
    }

    private int updateDrills(BizProjectRecordAddDto dto){
        UpdateWrapper<BizDrillRecord> drillUpdateWrapper= new UpdateWrapper<>();
        drillUpdateWrapper.lambda().eq(BizDrillRecord::getProjectId,dto.getProjectId()).set(BizDrillRecord::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizDrillRecordMapper.update(null,drillUpdateWrapper);


        if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
            IntStream.range(0, dto.getDrillRecords().size()).forEach(i -> {
                BizDrillRecordDto drillRecordDto = dto.getDrillRecords().get(i);
                BizDrillRecord bizDrillRecord  = new BizDrillRecord();
                BeanUtil.copyProperties(drillRecordDto, bizDrillRecord);
                bizDrillRecord.setDrillRecordId(null);
                bizDrillRecord.setStatus(0).setTravePointId(dto.getTravePointId()).setProjectId(dto.getProjectId()).setNo(i + 1); // i + 1 表示当前是第几个 drillRecord
                bizDrillRecordMapper.insert(bizDrillRecord);
            });
        }
        return 1;
    }


    private void updatePresetPoint(BizProjectRecordAddDto dto,Long projectId){
        UpdateWrapper<BizPresetPoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizPresetPoint::getProjectId,projectId)
                .set(BizPresetPoint::getDelFlag,BizBaseConstant.DELFLAG_Y);
        bizPresetPointMapper.update(new BizPresetPoint(),updateWrapper);

        try{

            BizPresetPoint presetPoint = new BizPresetPoint();
            //帮的线段 和 走向
            BizTunnelBar bar = bizTunnelBarMapper.selectById(dto.getBarId());
            //拿到当前到导线点和距离 DF09 -9 换算坐标
            BizTravePoint point = bizTravePointService.getById(dto.getTravePointId());
            //坐标获取 帮上的对应点   获取比例  根据走向 获取 孔在 帮上的坐标
            BigDecimal[] bigDecimals = getClosestPointOnSegment(point.getAxisx(),point.getAxisy(),bar.getStartx(), bar.getStarty(), bar.getEndx(), bar.getEndy());

            String uploadUrl = configService.selectConfigByKey(MapConfigConstant.map_bili);

            BigDecimal[] pointssa = getExtendedPoint(bigDecimals[0].toString(), bigDecimals[1].toString(), bar.getTowardAngle(), Double.parseDouble(dto.getConstructRange()), Double.parseDouble(uploadUrl));

            //根据 方位角 和 每米的 方位角 获取下一个点的坐标 ,
            //返回坐标组
            if(dto.getDrillRecords() != null && dto.getDrillRecords().size() > 0){
                String details = dto.getDrillRecords().get(0).getDetailJson();
                if(details != null && StrUtil.isNotBlank(details) && !details.equals("[]")){
                    JSONArray array = JSONUtil.parseArray(details);
                    List<Map<String,Object>> list = new ArrayList<>();

                    String lat = pointssa[0].toString();
                    String lon = pointssa[1].toString();
//                    BigDecimal[] pppp =  getExtendedPoint(lat,lon,bar.getDirectAngle(),1, Double.parseDouble(uploadUrl));
                    BigDecimal[] pppp =  pointssa;
                    lat = pppp[0].toString();
                    lon = pppp[1].toString();
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("x", lat);
                    map1.put("y", lon);
                    list.add(map1);
                    for (Object o : array) {
                        Integer bear_angle = JSONUtil.parseObj(o).getInt("bear_angle");
                        bear_angle = getAngle(bar.getDirectAngle(),bear_angle);
                        BigDecimal[] pppps = getExtendedPoint(lat,lon,bear_angle,1,Double.parseDouble(uploadUrl));
                        lat = pppps[0].toString();
                        lon = pppps[1].toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("x", lat);
                        map.put("y", lon);
                        list.add(map);
                    }
                    String str = JSONUtil.parseArray(list).toString();
                    presetPoint.setAxiss(str);
                }
            }

            //新版本,曲折一一对应
            if(point != null && point.getPointId() != null){
                Long dangerAreaId = bizTravePointService.judgeXYInArea(bigDecimals[0].toString(),bigDecimals[1].toString(),dto.getTunnelId());
//                Double ddd = new BigDecimal(bar.getDirectRange()).doubleValue();

                presetPoint.setConstructTime(dto.getConstructTime())
                        .setDrillType(dto.getDrillType())
                        .setTunnelId(dto.getTunnelId())
                        .setWorkfaceId(dto.getWorkfaceId())
                        .setProjectId(projectId)
                        .setDangerAreaId(dangerAreaId)
                        .setTunnelBarId(bar.getBarId());
                bizPresetPointMapper.insert(presetPoint);
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
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

        BizWorkface workface = bizWorkfaceMapper.selectById(dto.getWorkfaceId());

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
        if(tunnelIds == null || tunnelIds.size() == 0){
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(workface.getWorkfaceName()+"~煤粉量报表.xlsx","UTF-8"));
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                wb.set(new XSSFWorkbook());

//                wb.get().write(byteArrayOutputStream);
                byte[] excelData = byteArrayOutputStream.toByteArray();
                response.setContentLength(excelData.length);
                response.getOutputStream().write(excelData);
                wb.get().close();
            }
            return;
        }

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
                .between(StrUtil.isNotEmpty(dto.getEndTime()),BizProjectRecord::getConstructTime,dto.getStartTime(),dto.getEndTime())
                .innerJoin(BizDrillRecord.class, BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId);
        List<BizProjectCDMAP> cdmaps = bizProjectRecordMapper.selectJoinList(BizProjectCDMAP.class,projectRecordQueryWrapper);
//        Assert.isTrue(cdmaps != null && cdmaps.size() > 0, "没有记录");

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
                .between(StrUtil.isNotEmpty(dto.getEndTime()),BizProjectRecord::getConstructTime,dto.getStartTime(),dto.getEndTime())
                .innerJoin(BizDrillRecord.class, BizDrillRecord::getProjectId,BizProjectRecord::getProjectId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizProjectRecord::getTunnelId);
        List<BizProjectCDMAP> cdmaps1 = bizProjectRecordMapper.selectJoinList(BizProjectCDMAP.class,projectRecordQueryWrapper1);
//        Assert.isTrue(cdmaps != null && cdmaps.size() > 0, "没有记录");

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
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(workface.getWorkfaceName()+"~煤粉量报表.xlsx","UTF-8"));
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            if(wb.get() == null){
                wb.set(new XSSFWorkbook());
                byte[] excelData = byteArrayOutputStream.toByteArray();
                response.setContentLength(excelData.length);
                response.getOutputStream().write(excelData);
                wb.get().close();
                return;
            }
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
        UpdateWrapper<BizPlanPreset> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(BizPlanPreset::getDelFlag, BizBaseConstant.DELFLAG_Y)
                .eq(BizPlanPreset::getPlanId, planId);
        bizPlanPresetMapper.delete(updateWrapper);
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

    /**
     * 获取驳回原因
     * @param projectId 工程填报id
     * @return 返回结果
     */
    @Override
    public ReturnReasonDTO getReason(Long projectId) {
        ReturnReasonDTO returnReasonDTO = new ReturnReasonDTO();
        TeamAuditEntity teamAuditEntity = teamAuditMapper.selectOne(new LambdaQueryWrapper<TeamAuditEntity>()
                .eq(TeamAuditEntity::getProjectId, projectId)
                .eq(TeamAuditEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                .orderByDesc(TeamAuditEntity::getCreateTime)
                .last("LIMIT 1"));
        if (ObjectUtil.isNotNull(teamAuditEntity)) {
            if (teamAuditEntity.getAuditResult().equals(ConstantsInfo.AUDIT_REJECT)) {
                returnReasonDTO.setRejectTag("区队审核");
                returnReasonDTO.setRejectReason(teamAuditEntity.getRejectionReason());
            }
            if (teamAuditEntity.getAuditResult().equals(ConstantsInfo.AUDIT_SUCCESS)) {
                DepartmentAuditEntity departmentAuditEntity = departmentAuditMapper.selectOne(new LambdaQueryWrapper<DepartmentAuditEntity>()
                        .eq(DepartmentAuditEntity::getProjectId, projectId)
                        .eq(DepartmentAuditEntity::getTeamAuditId, teamAuditEntity.getTeamAuditId())
                        .orderByDesc(DepartmentAuditEntity::getCreateTime)
                        .last("LIMIT 1"));
                if (ObjectUtil.isNotNull(departmentAuditEntity)) {
                    if (departmentAuditEntity.getAuditResult().equals(ConstantsInfo.AUDIT_REJECT)) {
                        returnReasonDTO.setRejectTag("科室审核");
                        returnReasonDTO.setRejectReason(departmentAuditEntity.getRejectionReason());
                    }
                }
            }
        }
        return returnReasonDTO;
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












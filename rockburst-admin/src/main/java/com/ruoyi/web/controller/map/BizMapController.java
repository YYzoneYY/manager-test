package com.ruoyi.web.controller.map;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.push.GeTuiUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.domain.vo.BizPresetPointVo;
import com.ruoyi.system.domain.vo.BizWorkfaceSvgVo;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "map-地图")
@RestController
@RequestMapping("/map")
public class BizMapController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;
    @Autowired
    private IBizVideoService bizVideoService;
    @Autowired
    private IBizDrillRecordService bizDrillRecordService;






    @Resource
    private IBizPresetPointService bizPresetPointService;

    @Resource
    private ISysDictDataService sysDictDataService;

    @Resource
    private IBizDangerAreaService bizDangerAreaService;


    @Resource
    private IBizWorkfaceService bizWorkfaceService;

    @Resource
    private PlanService planService;

    @Resource
    private BizPlanPresetMapper bizPlanPresetMapper;
    @Autowired
    private BizPresetPointMapper bizPresetPointMapper;



    @Resource
    private IBizMineService bizMineService;

    @Resource
    private IBizMiningAreaService bizMiningAreaService;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Resource
    private TunnelService tunnelService;

    @Resource
    private IBizTravePointService bizTravePointService;

    @Resource
    private IBizTunnelBarService bizTunnelBarService;
    @Autowired
    private BizTravePointMapper bizTravePointMapper;
    @Autowired
    private BizDangerLevelMapper bizDangerLevelMapper;
    @Autowired
    private IYtFactorService ytFactorService;

    @Resource
    private BizDangerAreaMapper bizDangerAreaMapper;

    @Resource
    private GeTuiUtils geTuiUtils;


    @ApiOperation("测试推送")
    @GetMapping("/sssscc")
    public void pushOne(String cid, String title, String content) {
        geTuiUtils.pushMsg(cid, title, content);
    }

//
//    @ApiOperation("给起始和结束")
//    @GetMapping("/xxxxxx")
//    public R<?> xxxxxx(@RequestParam(required = false) Long startPointId,
//                                     @RequestParam(required = false) Double startMeter,
//                                     @RequestParam(required = false) Long endPointId,
//                                     @RequestParam(required = false) Double endMeter)
//    {
//        BizPresetPoint points =  bizTravePointService.getPointLatLon(startPointId,startMeter);
//        BizPresetPoint pointe = bizTravePointService.getPointLatLon(endPointId,endMeter);
//        Map<String, Object> map = new HashMap<>();
//        map.put("points", points);
//        map.put("pointe", pointe);
//        return R.ok(map);
//    }




    @ApiOperation("查询字典")
    @GetMapping("/dictDataList")
    public R<List<SysDictData>> list(SysDictData dictData)
    {

        List<SysDictData> list = sysDictDataService.selectDictDataList(dictData);
        return R.ok(list);
    }

    @ApiOperation("获取云图能量值")
    @GetMapping("/getyt")
    public R getyt(@ParameterObject YtFactor dto)
    {
        QueryWrapper<YtFactor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StrUtil.isNotEmpty(dto.getFactorType()),YtFactor::getFactorType, dto.getFactorType())
                .like(StrUtil.isNotEmpty(dto.getName()),YtFactor::getFactorType, dto.getName());
        List<YtFactor>  yt  =  ytFactorService.list(queryWrapper);
        return R.ok(yt);
    }


    @ApiOperation("查询影响因素列表")
//    @PreAuthorize("@ss.hasPermi('yt:factor:list')")
    @GetMapping("/ytList")
    public R<List<YtFactor>> list(@ParameterObject YtFactor dto)
    {
        QueryWrapper<YtFactor> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StrUtil.isNotEmpty(dto.getFactorType()),YtFactor::getFactorType, dto.getFactorType())
                .like(StrUtil.isNotEmpty(dto.getName()),YtFactor::getFactorType, dto.getName());
        List<YtFactor> list = ytFactorService.list(queryWrapper);
        return R.ok(list);
    }




    @ApiOperation("查询所有矿")
    @GetMapping("/getMine")
    public R<List<BizMine>> getMine()
    {
        QueryWrapper<BizMine> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizMine::getMineId, BizMine::getMineName, BizMine::getSvg,BizMine::getCenter);
        return R.ok(bizMineService.list(queryWrapper));
    }

    @ApiOperation("查询所有矿详情")
    @GetMapping("/getMineDetail/{mineId}")
    public R<BizMine> getMineDetail(@PathVariable("mineId") Long mineId)
    {
        return R.ok(bizMineService.getById(mineId));
    }

    @ApiOperation("查询危险等级")
    @GetMapping("/getLevel")
    public R<List<BizDangerLevel>> getLevel()
    {
        QueryWrapper<BizDangerLevel> queryWrapper = new QueryWrapper<>();
        return R.ok(bizDangerLevelMapper.selectList(new QueryWrapper<BizDangerLevel>().lambda()));
    }

    @ApiOperation("查询采区")
    @GetMapping("/getArea")
    public R<List<BizMiningArea>> getArea(@RequestParam(required = false) Long mineId)
    {
        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizMiningArea::getMiningAreaId, BizMiningArea::getMiningAreaName, BizMiningArea::getSvg,BizMiningArea::getCenter)
                .eq(mineId != null, BizMiningArea::getMineId, mineId);
        return R.ok(bizMiningAreaService.list(queryWrapper));
    }

    @ApiOperation("查询采区详情")
    @GetMapping("/getAreaDetail/{mineAreaId}")
    public R<BizMiningArea> getAreaDetail(@PathVariable("mineAreaId") Long mineAreaId)
    {

        return R.ok(bizMiningAreaService.getById(mineAreaId));
    }

    @ApiOperation("查询工作面")
    @GetMapping("/getWorkface")
    public R<List<BizWorkfaceSvgVo>> getWorkface(@RequestParam(required = false) Long mineAreaId,
                                            @RequestParam(required = false) Long mineId)
    {
        MPJLambdaWrapper<BizWorkface> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAs("t1",SysDictData::getDictLabel, BizWorkfaceSvgVo::getStatusName)
                .selectAs("t2",SysDictData::getDictLabel, BizWorkfaceSvgVo::getTypeName)
                .select(BizWorkface::getWorkfaceId, BizWorkface::getWorkfaceName, BizWorkface::getSvg,BizWorkface::getCenter,BizWorkface::getStatus,BizWorkface::getType)
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizWorkface::getStatus)
                .leftJoin(SysDictData.class,SysDictData::getDictValue,BizWorkface::getType)
                .in(SysDictData::getDictType,"workface_status","workface_type")
                .eq(mineAreaId != null, BizWorkface::getMiningAreaId, mineAreaId)
                .eq(mineId != null, BizWorkface::getMineId, mineId);
        return R.ok(bizWorkfaceMapper.selectJoinList(BizWorkfaceSvgVo.class,queryWrapper));
    }

    @ApiOperation("查询工作面详情")
    @GetMapping("/getWorkfaceDetail/{workfaceId}")
    public R<BizWorkface> getWorkfaceDetail(@PathVariable("workfaceId") Long workfaceId)
    {
        return R.ok(bizWorkfaceService.getById(workfaceId));
    }

    @ApiOperation("查询巷道")
    @GetMapping("/getTunnel")
    public R<List<TunnelEntity>> getTunnel(@RequestParam(required = false) Long workfaceId)
    {
        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
//                .select(TunnelEntity::getWorkfaceId, TunnelEntity::getWorkfaceName, TunnelEntity::getSvg,TunnelEntity::getCenter)
                .select(TunnelEntity::getTunnelId, TunnelEntity::getTunnelName)
                .eq(workfaceId != null, TunnelEntity::getWorkFaceId, workfaceId);
        return R.ok(tunnelService.list(queryWrapper));
    }


    @ApiOperation("查询巷道详情")
    @GetMapping("/getTunnelDetail/{tunnelId}")
    public R<TunnelEntity> getTunnelDetail(@PathVariable("tunnelId") Long tunnelId)
    {
        return R.ok(tunnelService.getById(tunnelId));
    }


    @ApiOperation("查询帮")
    @GetMapping("/getBar")
    public R<List<BizTunnelBar>> getBar(@RequestParam(required = false) Long tunnelId,
                                        @RequestParam(required = false) Long workfaceId)
    {
        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizTunnelBar::getBarId, BizTunnelBar::getBarName, BizTunnelBar::getSvg,BizTunnelBar::getCenter)
                .eq(workfaceId != null, BizTunnelBar::getWorkfaceId, workfaceId)
                .eq(tunnelId != null, BizTunnelBar::getTunnelId, tunnelId);
        return R.ok(bizTunnelBarService.list(queryWrapper));
    }


    @ApiOperation("查询帮详情")
    @GetMapping("/getBarDetail/{barId}")
    public R<BizTunnelBar> getBarDetail(@PathVariable("barId") Long barId)
    {
        return R.ok(bizTunnelBarService.getById(barId));
    }

    @ApiOperation("查询危险区")
    @GetMapping("/getDangerArea")
    public R<List<BizDangerArea>> getDangerArea(@RequestParam(required = false) Long tunnelId,
                                              @RequestParam(required = false) Long workfaceId)
    {
        QueryWrapper<BizDangerArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizDangerArea::getDangerAreaId, BizDangerArea::getNo, BizDangerArea::getSvg,BizDangerArea::getCenter)
                .eq(workfaceId != null, BizDangerArea::getWorkfaceId, workfaceId)
                .eq(tunnelId != null, BizDangerArea::getTunnelId, tunnelId);
        return R.ok(bizDangerAreaService.list(queryWrapper));
    }

    @ApiOperation("查询危险区详情")
    @GetMapping("/getDangerAreaDetail/{dangerAreaId}")
    public R<BizDangerArea> getDangerAreaDetail(@PathVariable("dangerAreaId") Long dangerAreaId)
    {

        return R.ok(bizDangerAreaService.getById(dangerAreaId));
    }



    @ApiOperation("根据计划id查询危险区域")
    @GetMapping("/getAreaByPlan")
    public R<List<BizDangerAreaVo>> getAreaByPlan(@RequestParam(value = "工作面id", required = true) Long workfaceId,
                                                  @RequestParam(value = "危险等级", required = false) String level)
    {
        BizDangerAreaDto areaDto = new BizDangerAreaDto();
        areaDto.setWorkfaceId(workfaceId);
        if(StrUtil.isNotBlank(level)){
            areaDto.setLevel(level);
        }
        return R.ok(bizDangerAreaService.selectEntityCheckList(areaDto));
    }

    @ApiOperation("查询所有预设点")
    @GetMapping("/getPrePoint")
    public R<List<BizPresetPoint>> getPrePoint()
    {
        ;
        return R.ok(bizPresetPointMapper.selectList(new QueryWrapper<BizPresetPoint>()));
    }


    @ApiOperation("三合一")
    @GetMapping("/getshy")
    public R<Map<String,Object>> getshy(@RequestParam( required = false) String constructType,
                                        @RequestParam( required = false) String year,
                                        @RequestParam( required = false) String month,
                                        @RequestParam( required = false) Long workfaceId,
                                        @RequestParam(required = false) String drillType)
    {

        Map<String,Object> map = new HashMap<>();

        Date start = null;
        Date end = null;
        start = getStart(year, month);
        end = getEnd(year, month);
        // 查询施工钻孔
        QueryWrapper<BizPresetPoint> queryWrapperPro = new QueryWrapper<>();
        queryWrapperPro.lambda()
                .eq(StrUtil.isNotEmpty(drillType), BizPresetPoint::getDrillType,drillType)
                .eq(workfaceId != null, BizPresetPoint::getWorkfaceId,workfaceId)
                .isNotNull(BizPresetPoint::getProjectId)
                .between(end != null, BizPresetPoint::getConstructTime,start,end);
        List<BizPresetPoint> points = bizPresetPointMapper.selectList(queryWrapperPro);
        List<BizPresetPointVo> vos = new ArrayList<>();
        if(points != null && points.size() > 0){
            List<Long> projectIds = points.stream().map(BizPresetPoint::getProjectId).collect(Collectors.toList());
            QueryWrapper<BizProjectRecord> projectRecordQueryWrapper = new QueryWrapper<>();

            projectRecordQueryWrapper.lambda().in(BizProjectRecord::getProjectId,projectIds)
                    .eq(BizProjectRecord::getConstructType,constructType)
                    .ne(BizProjectRecord::getIsHead,1);
            List<BizProjectRecord> records = bizProjectRecordService.listDeep(projectRecordQueryWrapper);

//            List<BizProjectRecord> records = bizProjectRecordService.listByIdsDeep(projectIds);
            final Map<Long, List<BizProjectRecord>>[] groupedByProjectId = new Map[]{records.stream()
                    .collect(Collectors.groupingBy(BizProjectRecord::getProjectId))};
            for (BizPresetPoint point : points) {

                BizDangerArea bizDangerArea = bizDangerAreaMapper.selectOne(new LambdaQueryWrapper<BizDangerArea>()
                        .eq(BizDangerArea::getDangerAreaId, point.getDangerAreaId())
                        .eq(BizDangerArea::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                BizDangerLevel bizDangerLevel = bizDangerLevelMapper.selectOne(new LambdaQueryWrapper<BizDangerLevel>()
                        .eq(BizDangerLevel::getLevel, bizDangerArea.getLevel())
                        .eq(BizDangerLevel::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                BizPresetPointVo pointVo = new BizPresetPointVo();
                BeanUtil.copyProperties(point,pointVo);
                List<BizProjectRecord> projectRecords =  groupedByProjectId[0].get(point.getProjectId());
                if(projectRecords != null && projectRecords.size() > 0){
                    BizProjectRecord projectRecord = projectRecords.get(0);
                    pointVo.setAccepter(projectRecord.getAccepterEntity() == null ? "" : projectRecord.getAccepterEntity().getName())
                            .setBigbanger(projectRecord.getBigbangerEntity() == null ? "" : projectRecord.getBigbangerEntity().getName())
                            .setProjecrHeader(projectRecord.getProjecrHeaderEntity() == null ? "" : projectRecord.getProjecrHeaderEntity().getName())
                            .setSecurityer(projectRecord.getSecurityerEntity() == null ? "" : projectRecord.getSecurityerEntity().getName())
                            .setWorker(projectRecord.getWorkerEntity() == null ? "" : projectRecord.getWorkerEntity().getName())
                            .setConstructionUnit(projectRecord.getConstructionUnit() == null ? "" : projectRecord.getConstructionUnit().getConstructionUnitName())
                            .setWorkfaceName(projectRecord.getWorkfaceName())
                            .setTunnelName(projectRecord.getTunnelName())
                            .setPointName(projectRecord.getTravePoint() == null ? "" : projectRecord.getTravePoint().getPointName())
                            .setDrillNum(projectRecord.getDrillNum())
                            .setLevel(bizDangerLevel.getLevel())
                            .setColor(bizDangerLevel.getColor());
                    vos.add(pointVo);
                }

            }
        }
        map.put("ProjectPoints",vos);

        // 查询迎头钻孔
        QueryWrapper<BizPresetPoint> queryWrapperProYt = new QueryWrapper<>();
        queryWrapperProYt.lambda()
                .isNotNull( BizPresetPoint::getAxiss)
                .eq( StrUtil.isNotEmpty(drillType), BizPresetPoint::getDrillType,drillType )
                .eq(workfaceId != null, BizPresetPoint::getWorkfaceId,workfaceId)
                .isNotNull(BizPresetPoint::getProjectId)
                .between(end != null, BizPresetPoint::getConstructTime,start,end)
                .orderByDesc(BizPresetPoint::getConstructTime);
        List<BizPresetPoint> pointYts = bizPresetPointMapper.selectList(queryWrapperProYt);
        List<BizPresetPointVo> ytvos = new ArrayList<>();
        if(pointYts != null && pointYts.size() > 0){
            pointYts = pointYts.size() > 3 ? pointYts.subList(0, 3) : pointYts;
            List<Long> projectIds = pointYts.stream().map(BizPresetPoint::getProjectId).collect(Collectors.toList());
            QueryWrapper<BizProjectRecord> projectRecordQueryWrapper = new QueryWrapper<>();
//            List<BizProjectRecord> records = bizProjectRecordService.listByIdsDeep(projectIds);

            projectRecordQueryWrapper.lambda().in(BizProjectRecord::getProjectId,projectIds)
                    .eq(BizProjectRecord::getConstructType,constructType)
                    .eq(BizProjectRecord::getIsHead,1);
            List<BizProjectRecord> records = bizProjectRecordService.listDeep(projectRecordQueryWrapper);

            final Map<Long, List<BizProjectRecord>>[] groupedByProjectId = new Map[]{records.stream()
                    .collect(Collectors.groupingBy(BizProjectRecord::getProjectId))};
            for (BizPresetPoint point : pointYts) {

                BizDangerArea bizDangerArea = bizDangerAreaMapper.selectOne(new LambdaQueryWrapper<BizDangerArea>()
                        .eq(BizDangerArea::getDangerAreaId, point.getDangerAreaId())
                        .eq(BizDangerArea::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                BizDangerLevel bizDangerLevel = bizDangerLevelMapper.selectOne(new LambdaQueryWrapper<BizDangerLevel>()
                        .eq(BizDangerLevel::getLevel, bizDangerArea.getLevel())
                        .eq(BizDangerLevel::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                BizPresetPointVo pointVo = new BizPresetPointVo();
                BeanUtil.copyProperties(point,pointVo);
                List<BizProjectRecord> projectRecords =  groupedByProjectId[0].get(point.getProjectId());
                if(projectRecords != null && projectRecords.size() > 0){
                    BizProjectRecord projectRecord = projectRecords.get(0);
                    pointVo
                            .setAccepter(projectRecord.getAccepterEntity() == null ? "" : projectRecord.getAccepterEntity().getName())
                            .setBigbanger(projectRecord.getBigbangerEntity() == null ? "" : projectRecord.getBigbangerEntity().getName())
                            .setProjecrHeader(projectRecord.getProjecrHeaderEntity() == null ? "" : projectRecord.getProjecrHeaderEntity().getName())
                            .setSecurityer(projectRecord.getSecurityerEntity() == null ? "" : projectRecord.getSecurityerEntity().getName())
                            .setWorker(projectRecord.getWorkerEntity() == null ? "" : projectRecord.getWorkerEntity().getName())
                            .setConstructionUnit(projectRecord.getConstructionUnit() == null ? "" : projectRecord.getConstructionUnit().getConstructionUnitName())
                            .setWorkfaceName(projectRecord.getWorkfaceName())
                            .setTunnelName(projectRecord.getTunnelName())
                            .setPointName(projectRecord.getTravePoint() == null ? "" : projectRecord.getTravePoint().getPointName())
                            .setDrillNum(projectRecord.getDrillNum())
                            .setLevel(bizDangerLevel.getLevel())
                            .setColor(bizDangerLevel.getColor());
                    ytvos.add(pointVo);
                }

            }
        }
        map.put("ProjectPointYts",ytvos);

        // 计划内危险区
        Long startc;
        Long endc;
        if(end == null){
            endc = null;
            startc = null;
        }else {
            startc = start.getTime();
            endc = end.getTime();
        }

        QueryWrapper<PlanEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(PlanEntity::getPlanId,PlanEntity::getPlanName,PlanEntity::getType)
                .eq(StrUtil.isNotEmpty(year),PlanEntity::getAnnual,year)
                .eq(workfaceId != null, PlanEntity::getWorkFaceId,workfaceId)
                .eq(PlanEntity::getType, constructType)
                .eq(StrUtil.isNotEmpty(drillType),PlanEntity::getDrillType,drillType)
                .and(startc != null,i->i.le(PlanEntity::getStartTime,endc).ge(PlanEntity::getEndTime,startc));
        List<PlanEntity> planEntities = planService.list(queryWrapper);
        List<Long> dangerAreaIds = new ArrayList<>();
        if(planEntities != null && planEntities.size() > 0) {
            for (PlanEntity planEntity : planEntities) {
                QueryWrapper<BizPlanPreset> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(BizPlanPreset::getPlanId, planEntity.getPlanId());
                List<BizPlanPreset> planPresets = bizPlanPresetMapper.selectList(queryWrapper1);
                List<Long> areaIds = new ArrayList<>();
                if (planPresets != null && planPresets.size() > 0) {
                    areaIds = planPresets.stream().map(BizPlanPreset::getDangerAreaId).collect(Collectors.toList());
                    areaIds = areaIds.stream().distinct().collect(Collectors.toList());
                    dangerAreaIds.addAll(areaIds);
                }
            }
            if(dangerAreaIds != null && dangerAreaIds.size() > 0){
                dangerAreaIds.stream().distinct().collect(Collectors.toList());
                QueryWrapper<BizDangerArea> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().in(dangerAreaIds != null && dangerAreaIds.size() >0, BizDangerArea::getDangerAreaId, dangerAreaIds);
                List<BizDangerAreaVo> areas = bizDangerAreaService.selectEntityListVo(dangerAreaIds);
                map.put("areas",areas);
            }

        }

        // 计划内预设点
        List<BizPlanPreset> vo1s = new ArrayList<>();
        //循环计划
        if(planEntities != null && planEntities.size() > 0){
            for (PlanEntity planEntity : planEntities) {
                QueryWrapper<BizPlanPreset> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(BizPlanPreset::getPlanId,planEntity.getPlanId());
                List<BizPlanPreset> planPresets = bizPlanPresetMapper.selectList(queryWrapper1);
                vo1s.addAll(planPresets);
            }
            map.put("planPrePoint",vo1s);
        }

        return R.ok(map);
    }


    @ApiOperation("根据条件查询填报孔")
    @GetMapping("/getProjectByWorkface")
    public R<List<BizPresetPoint>> getProjectBy(@RequestParam( required = false) String year,
                                                @RequestParam( required = false) String month,
                                                @RequestParam( required = false) String drillType)
    {
        Date start = null;
        Date end = null;
        if( StrUtil.isNotBlank(month)){
            start = getStart(year, month);
            end = getEnd(year, month);
        }
        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizPresetPoint::getDrillType,drillType)
                .between(BizPresetPoint::getCreateTime,start,end);
        return R.ok(bizPresetPointMapper.selectList(queryWrapper));
    }

    @ApiOperation("根据危险等级和工作面id查询危险区域")
    @GetMapping("/getAreaByWorkface")
    public R<List<BizDangerAreaVo>> getAreaByWorkface(@RequestParam( required = false) String year,
                                                      @RequestParam( required = false) String month,
                                                      @RequestParam(required = false) String drillType)
    {
        Long start;
        Long end;
        if( StrUtil.isNotBlank(month)){
            start = getStart(year, month).getTime();
            end = getEnd(year, month).getTime();
        } else {
            end = 0l;
            start = 0l;
        }
        QueryWrapper<PlanEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(PlanEntity::getPlanId,PlanEntity::getPlanName,PlanEntity::getType)
                .eq(StrUtil.isNotEmpty(year),PlanEntity::getAnnual,year)
                .eq(StrUtil.isNotEmpty(drillType),PlanEntity::getDrillType,drillType)
                .and(StrUtil.isNotBlank(month),i->i.ge(PlanEntity::getStartTime,start).or().le(PlanEntity::getStartTime,end))
                .and(StrUtil.isNotBlank(month),i->i.ge(PlanEntity::getEndTime,start).or().le(PlanEntity::getEndTime,end));
        List<PlanEntity> planEntities = planService.list(queryWrapper);
        List<Long> dangerAreaIds = new ArrayList<>();
        if(planEntities != null && planEntities.size() > 0){
            for (PlanEntity planEntity : planEntities) {
                QueryWrapper<BizPlanPreset> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(BizPlanPreset::getPlanId,planEntity.getPlanId());
                List<BizPlanPreset> planPresets = bizPlanPresetMapper.selectList(queryWrapper1);
                List<Long> areaIds = new ArrayList<>();
                if(planPresets != null && planPresets.size() > 0){
                    areaIds = planPresets.stream().map(BizPlanPreset::getDangerAreaId).collect(Collectors.toList());
                    areaIds = areaIds.stream().distinct().collect(Collectors.toList());
                    dangerAreaIds.addAll(areaIds);
                }
            }
            dangerAreaIds.stream().distinct().collect(Collectors.toList());
            QueryWrapper<BizDangerArea> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda().in(BizDangerArea::getDangerAreaId,dangerAreaIds);
            return  R.ok(bizDangerAreaService.selectEntityListVo(dangerAreaIds));
        }
        return null;

    }

    @ApiOperation("获取工程填报记录地图信息")
//    @PreAuthorize("@ss.hasPermi('project:record:map')")
    @GetMapping(value = "/map/{projectId}")
    public R<BizPresetPointVo> getmapInfo(@PathVariable("projectId") Long projectId)
    {
        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizPresetPoint::getProjectId, projectId);
        List<BizPresetPoint> bizPresetPoints = bizPresetPointService.list(queryWrapper);
        if(bizPresetPoints != null && bizPresetPoints.size() > 0){
            BizPresetPoint p = bizPresetPoints.get(0);
            BizPresetPointVo vo = new BizPresetPointVo();
            BeanUtil.copyProperties(p,vo);

            if(p.getDangerAreaId() != null){
                BizDangerArea area = bizDangerAreaService.getById(p.getDangerAreaId());
                QueryWrapper<BizDangerLevel> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.lambda().eq(BizDangerLevel::getLevel,area.getLevel());
                List<BizDangerLevel> levels = bizDangerLevelMapper.selectList(queryWrapper2);
                if(levels != null && levels.size() > 0){
                    vo.setColor(levels.get(0).getColor());
                }
            }
            BizProjectRecord record = bizProjectRecordService.getByIdDeep(projectId);
            vo.setAccepter(record.getAccepterEntity() == null ? "" : record.getAccepterEntity().getName())
                    .setConstructType(record.getConstructTypeName())
                    .setBigbanger(record.getBigbangerEntity() == null ? "" : record.getBigbangerEntity().getName())
                    .setProjecrHeader(record.getProjecrHeaderEntity() == null ? "" : record.getProjecrHeaderEntity().getName())
                    .setSecurityer(record.getSecurityerEntity() == null ? "" : record.getSecurityerEntity().getName())
                    .setWorker(record.getWorkerEntity() == null ? "" : record.getWorkerEntity().getName())
                    .setConstructionUnit(record.getConstructionUnit() == null ? "" : record.getConstructionUnit().getConstructionUnitName())
                    .setWorkfaceName(record.getWorkfaceName())
                    .setTunnelName(record.getTunnelName())
                    .setDrillType(record.getDrillTypeName())
                    .setPointName(record.getTravePoint() == null ? "" : record.getTravePoint().getPointName())
                    .setDrillNum(record.getDrillNum());
            return R.ok(vo);
        }
        return R.ok(null);
    }


    @ApiOperation("根据计划筛选预设点")
    @GetMapping("/getPrePointByPlan")
    public R getPrePointByPlan(@RequestParam( required = false) String year,
                               @RequestParam( required = false) String month,
                               @RequestParam( required = false) String drillType)
    {

        Long start;
        Long end;
        if( StrUtil.isNotBlank(month)){
            start = getStart(year, month).getTime();
            end = getEnd(year, month).getTime();
        } else {
            end = 0l;
            start = 0l;
        }
        QueryWrapper<PlanEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(PlanEntity::getPlanId,PlanEntity::getPlanName,PlanEntity::getType)
                .eq(StrUtil.isNotEmpty(year),PlanEntity::getAnnual,year)
                .eq(StrUtil.isNotEmpty(drillType),PlanEntity::getDrillType,drillType)
                .and(StrUtil.isNotBlank(month),i->i.ge(PlanEntity::getStartTime,start).or().le(PlanEntity::getStartTime,end))
                .and(StrUtil.isNotBlank(month),i->i.ge(PlanEntity::getEndTime,start).or().le(PlanEntity::getEndTime,end));
        List<PlanEntity> planEntities = planService.list(queryWrapper);

        List<BizPlanPreset> vo1s = new ArrayList<>();
        //循环计划
        if(planEntities != null && planEntities.size() > 0){
            for (PlanEntity planEntity : planEntities) {
                QueryWrapper<BizPlanPreset> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.lambda().eq(BizPlanPreset::getPlanId,planEntity.getPlanId());
                List<BizPlanPreset> planPresets = bizPlanPresetMapper.selectList(queryWrapper1);
                vo1s.addAll(planPresets);
            }
        }
        return R.ok(vo1s);
    }


    Date getStart(String year,String month){
        if(StrUtil.isEmpty(year)){
            return null;
        }

        String label = sysDictDataService.selectDictLabel("year",year);

        if (month == null) {
            Date startDate = DateUtil.beginOfYear(DateUtil.parse(label + "-01-01"));
            return startDate;
        } else {
            Date startDate = DateUtil.beginOfMonth(DateUtil.parse(label + "-" + month + "-01"));
            return startDate;
        }
    }

    Date getEnd(String year, String month) {
        if(StrUtil.isEmpty(year)){
            return null;
        }
        String label = sysDictDataService.selectDictLabel("year", year);
        if (month == null) {
            Date endDate = DateUtil.endOfYear(DateUtil.parse(label + "-12-31"));
            return endDate;
        } else {
            Date endDate = DateUtil.endOfMonth(DateUtil.parse(label + "-" + month + "-01"));
            return endDate;
        }
    }









}

package com.ruoyi.web.controller.map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.domain.vo.BizWorkfaceSvgVo;
import com.ruoyi.system.mapper.BizPlanPresetMapper;
import com.ruoyi.system.mapper.BizPresetPointMapper;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private IBizTunnelBarService bizTunnelBarService;



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


    @ApiOperation("根据条件查询填报孔")
    @GetMapping("/getProjectByWorkface")
    public R<List<BizPresetPoint>> getProjectBy(@RequestParam(value = "年份", required = false) String year,
                                                @RequestParam(value = "月度", required = false) String month,
                                                @RequestParam(value = "施工钻孔类型", required = false) String drillType)
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
    public R<List<BizDangerAreaVo>> getAreaByWorkface(@RequestParam(value = "年份", required = false) String year,
                                                      @RequestParam(value = "月度", required = false) String month,
                                                      @RequestParam(value = "施工钻孔类型", required = false) String drillType)
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


    @ApiOperation("根据计划筛选预设点")
    @GetMapping("/getPrePointByPlan")
    public R getPrePointByPlan(@RequestParam(value = "年份", required = false) String year,
                               @RequestParam(value = "月度", required = false) String month,
                               @RequestParam(value = "施工钻孔类型", required = false) String drillType)
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
        String label = sysDictDataService.selectDictLabel("year",year);
        Date startDate = DateUtil.beginOfMonth(DateUtil.parse(label + "-" + month + "-01"));
        return startDate;
        // 获取指定年份和月份的结束时间（当月的最后一天）
    }

    Date getEnd(String year,String month){
        String label = sysDictDataService.selectDictLabel("year",year);

        Date endDate = DateUtil.endOfMonth(DateUtil.parse(label + "-" + month + "-01"));
        return endDate;
        // 获取指定年份和月份的结束时间（当月的最后一天）
    }









}

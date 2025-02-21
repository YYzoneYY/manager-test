package com.ruoyi.web.controller.projectFill;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizPlanPreset;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.mapper.BizPlanPresetMapper;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 工程填报记录Controller
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Api(tags = "project-防冲管理")
@RestController
@RequestMapping("/project/wash")
public class BizWashProofController extends BaseController
{
    @Autowired
    private IBizProjectRecordService bizProjectRecordService;
    @Autowired
    private IBizVideoService bizVideoService;
    @Autowired
    private IBizDrillRecordService bizDrillRecordService;

    @Resource
    PlanPastService planPastService;

    @Resource
    private IBizDangerAreaService bizDangerAreaService;


    @Resource
    private IBizWorkfaceService bizWorkfaceService;

    @Resource
    private PlanService planService;

    @Resource
    private BizPlanPresetMapper bizPlanPresetMapper;


//    @ApiOperation("防冲管理")
//    @PreAuthorize("@ss.hasPermi('project:wash:workfacelist')")
//    @GetMapping("/workfacelist")
//    public R<MPage<BizProjectRecordListVo>> workfacelist(@ParameterObject BizWashProofDto dto, Pagination pagination)
//    {
//        return R.ok(bizProjectRecordService.selectproList(new BasePermission(), dto,pagination));
//    }

    @ApiOperation("根据规划查询工作面")
    @GetMapping("/getWorkfaceByScheme")
    public R<MPage<BizWorkface>> getWorkfaceByScheme(@RequestParam(value = "规划", required = false) String scheme,
                                                       @RequestParam(value = "工作面名称", required = false) String workfaceName,
                                                       @ParameterObject  Pagination pagination)
    {
        return R.ok(bizWorkfaceService.getWorkfaceByScheme(scheme, workfaceName, pagination));
    }


    @ApiOperation("工作面地图位置")
    @GetMapping(value = "/{workfaceId}")
    public R getInfo(@PathVariable("workfaceId") Long workfaceId)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName,BizWorkface::getSvg)
                .eq(BizWorkface::getWorkfaceId, workfaceId);
        return R.ok(bizWorkfaceService.getBaseMapper().selectById(workfaceId));
    }



    @ApiOperation("根据危险等级和工作面id查询危险区域")
    @GetMapping("/getAreaByWorkface")
    public R<List<BizDangerAreaVo>> getAreaByWorkface(@RequestParam(value = "工作面id", required = true) Long workfaceId,
                                            @RequestParam(value = "危险等级", required = false) String level)
    {
        BizDangerAreaDto areaDto = new BizDangerAreaDto();
        areaDto.setWorkfaceId(workfaceId);
        if(StrUtil.isNotBlank(level)){
            areaDto.setLevel(level);
        }
        return R.ok(bizDangerAreaService.selectEntityCheckList(areaDto));
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
        Date startDate = DateUtil.beginOfMonth(DateUtil.parse(year + "-" + month + "-01"));
        return startDate;
        // 获取指定年份和月份的结束时间（当月的最后一天）
    }

    Date getEnd(String year,String month){
        Date endDate = DateUtil.endOfMonth(DateUtil.parse(year + "-" + month + "-01"));
        return endDate;
        // 获取指定年份和月份的结束时间（当月的最后一天）
    }





}

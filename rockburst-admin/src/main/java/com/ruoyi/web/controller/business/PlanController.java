package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.service.PlanPastService;
import com.ruoyi.system.service.PlanService;
import com.ruoyi.system.service.RelatesInfoService;
import io.swagger.annotations.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Api(tags = "计划编制")
@RestController
@RequestMapping("/engineeringPlan")
public class PlanController {

    @Resource
    private PlanPastService planPastService;

    @Resource
    private RelatesInfoService relatesInfoService;

    @Resource
    private PlanService planService;


//    @ApiOperation(value = "计划新增", notes = "计划新增")
//    @PreAuthorize("@ss.hasPermi('engineeringPlan:addPlan')")
//    @PostMapping(value = "/addPlan")
//    public R<Object> addPlan(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) PlanDTO planDTO){
//        return R.ok(this.planService.insertPlan(planDTO));
//    }
//
//    @ApiOperation(value = "计划修改", notes = "计划修改")
//    @PreAuthorize("@ss.hasPermi('engineeringPlan:updatePlan')")
//    @PutMapping(value = "/updatePlan")
//    public R<Object> updatePlan(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class}) PlanDTO planDTO){
//        return R.ok(this.planService.updatePlan(planDTO));
//    }

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @PreAuthorize("@ss.hasPermi('engineeringPlan:queryById')")
    @GetMapping(value = "/queryById")
    public R<Object> queryById(@ApiParam(name = "planId", value = "计划id", required = true)
                                   @RequestParam Long planId){
        return R.ok(this.planService.queryById(planId));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PreAuthorize("@ss.hasPermi('engineeringPlan:queryPage')")
    @PostMapping(value = "/queryPage")
    public R<Object> queryPage(@RequestBody SelectNewPlanDTO selectNewPlanDTO,
                               @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize){
        return R.ok(this.planService.queryPage(new BasePermission(), selectNewPlanDTO, pageNum, pageSize));
    }


    @ApiOperation(value = "撤回", notes = "撤回")
    @PreAuthorize("@ss.hasPermi('engineeringPlan:withdraw')")
    @GetMapping(value = "/withdraw")
    public R<Object> withdraw(@ApiParam(name = "planId", value = "计划id", required = true) @RequestParam Long planId){
        return R.ok(this.planService.withdraw(planId));
    }

    @ApiOperation(value = "删除计划", notes = "删除计划")
    @PreAuthorize("@ss.hasPermi('engineeringPlan:deletePlan')")
    @DeleteMapping(value = "/deletePlan")
    public R<Boolean> deletePlan(@ApiParam(name = "planIds", value = "计划id数组", required = true) @RequestParam Long[] planIds){
        return R.ok(this.planService.deletePlan(planIds));
    }

    @ApiOperation(value = "获取工程预警下拉列表", notes = "获取工程预警下拉列表")
    @GetMapping(value = "/getProjectWarnChoiceList")
    public R<List<ProjectWarnChoiceListDTO>> getProjectWarnChoiceList() {
        return R.ok(this.planService.getProjectWarnChoiceList());
    }

//    @ApiOperation(value = "获取计划中已使用的导线点", notes = "获取计划中已使用的导线点")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "type", value = "类型", required = true),
//            @ApiImplicitParam(name = "planType", value = "计划类型", required = true),
//            @ApiImplicitParam(name = "tunnelId", value = "巷道id", required = true)
//    })
//    @GetMapping(value = "/getTraversePoint")
//    public R<Object> getTraversePoint(@RequestParam(value = "type") String type,
//                                      @RequestParam(value = "planType") String planType,
//                                      @RequestParam(value = "tunnelId") Long tunnelId) {
//       return R.ok(this.relatesInfoService.getTraversePoint(planType, type, tunnelId));
//    }

//    @ApiOperation(value = "根据导线点获取计划", notes = "根据导线点获取计划")
//    @ApiImplicitParams(value = {
//            @ApiImplicitParam(name = "traversePoint", value = "点", required = true),
//            @ApiImplicitParam(name = "distance", value = "距离", required = true)
//    })
//    @GetMapping(value = "/getPlanByPoint")
//    public R<Object> getPlanByPoint(@RequestParam String traversePoint,
//                                    @RequestParam String distance) {
//        return R.ok(this.planPastService.getPlanByPoint(traversePoint, distance));
//    }
}
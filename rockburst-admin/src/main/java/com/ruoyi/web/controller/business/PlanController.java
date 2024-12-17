package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.service.PlanService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    private PlanService planService;


    @ApiOperation(value = "计划新增", notes = "计划新增")
    @PostMapping(value = "/addPlan")
    public R<Object> addPlan(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) PlanDTO planDTO){
        return R.ok(this.planService.insertPlan(planDTO));
    }

    @ApiOperation(value = "计划修改", notes = "计划修改")
    @PutMapping(value = "/updatePlan")
    public R<Object> updatePlan(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class}) PlanDTO planDTO){
        return R.ok(this.planService.updatePlan(planDTO));
    }

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @GetMapping(value = "/queryById")
    public R<Object> queryById(@ApiParam(name = "engineeringPlanId", value = "计划id", required = true)
                                   @RequestParam Long engineeringPlanId){
        return R.ok(this.planService.queryById(engineeringPlanId));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/queryPage")
    public R<Object> queryPage(@RequestBody SelectPlanDTO selectPlanDTO,
                                @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize){
        return R.ok(this.planService.queryPage(selectPlanDTO, pageNum, pageSize));
    }


    @ApiOperation(value = "撤回", notes = "撤回")
    @GetMapping(value = "/withdraw")
    public R<Object> withdraw(@ApiParam(name = "planId", value = "计划id", required = true) @RequestParam Long planId){
        return R.ok(this.planService.withdraw(planId));
    }

    @ApiOperation(value = "删除计划", notes = "删除计划")
    @DeleteMapping(value = "/deletePlan")
    public R<Boolean> deletePlan(@ApiParam(name = "planIds", value = "计划id数组", required = true) @RequestParam Long[] planIds){
        return R.ok(this.planService.deletePlan(planIds));
    }
}
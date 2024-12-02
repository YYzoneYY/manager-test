package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.EngineeringPlanDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;
import com.ruoyi.system.service.EngineeringPlanService;
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
public class EngineeringPlanController {

    @Resource
    private EngineeringPlanService engineeringPlanService;


    @ApiOperation(value = "计划新增", notes = "计划新增")
    @PostMapping(value = "/addPlan")
    public R<Object> addPlan(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) EngineeringPlanDTO engineeringPlanDTO){
        return R.ok(this.engineeringPlanService.insertPlan(engineeringPlanDTO));
    }

    @ApiOperation(value = "计划修改", notes = "计划修改")
    @PostMapping(value = "/updatePlan")
    public R<Object> updatePlan(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class}) EngineeringPlanDTO engineeringPlanDTO){
        return R.ok(this.engineeringPlanService.updatePlan(engineeringPlanDTO));
    }

    @ApiOperation(value = "根据id查询", notes = "根据id查询")
    @GetMapping(value = "/queryById")
    public R<Object> queryById(@ApiParam(name = "engineeringPlanId", value = "计划id", required = true) @RequestParam Long engineeringPlanId){
        return R.ok(this.engineeringPlanService.queryById(engineeringPlanId));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping(value = "/queryPage")
    public R<Object> queryPage(@RequestBody SelectPlanDTO selectPlanDTO,
                                @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                @ApiParam(name = "pageSize", value = "页数", required = true) @RequestParam Integer pageSize){
        return R.ok(this.engineeringPlanService.queryPage(selectPlanDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "提交审核", notes = "提交审核")
    @GetMapping(value = "/submitForReview")
    public R<Object> submitForReview(@ApiParam(name = "engineeringPlanId", value = "计划id", required = true) @RequestParam Long engineeringPlanId){
        return R.ok(this.engineeringPlanService.submitForReview(engineeringPlanId));
    }

    @ApiOperation(value = "撤回", notes = "撤回")
    @GetMapping(value = "/withdraw")
    public R<Object> withdraw(@ApiParam(name = "engineeringPlanId", value = "计划id", required = true) @RequestParam Long engineeringPlanId){
        return R.ok(this.engineeringPlanService.withdraw(engineeringPlanId));
    }

    @ApiOperation(value = "删除计划", notes = "删除计划")
    @DeleteMapping(value = "/deletePlan")
    public R<Boolean> deletePlan(@ApiParam(name = "engineeringPlanIds", value = "计划id数组", required = true) @RequestParam Long[] engineeringPlanIds){
        return R.ok(this.engineeringPlanService.deletePlan(engineeringPlanIds));
    }
}
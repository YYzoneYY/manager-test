package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.SupportResistanceDTO;
import com.ruoyi.system.service.SupportResistanceService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Api(tags = "工作面支架阻力")
@RestController
@RequestMapping(value = "/supportResistance")
public class SupportResistanceController {

    @Resource
    private SupportResistanceService supportResistanceService;

    @ApiOperation(value = "新增工作面支架阻力测点", notes = "新增工作面支架阻力测点")
    @PostMapping(value = "/add")
    public R<Object> addMeasure(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class})
                                SupportResistanceDTO supportResistanceDTO) {
        return R.ok(this.supportResistanceService.addMeasure(supportResistanceDTO));
    }

    @ApiOperation(value = "工作面支架阻力测点修改", notes = "工作面支架阻力测点修改")
    @PutMapping(value = "/update")
    public R<Integer> updateMeasure(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class})
                                     SupportResistanceDTO supportResistanceDTO) {
        return R.ok(this.supportResistanceService.updateMeasure(supportResistanceDTO));
    }

    @ApiOperation(value = "分页查询数据列表接口", notes = "分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping(value = "/pageQueryList")
    public R<Object> pageQueryList(@RequestBody MeasureSelectDTO measureSelectDTO,
                                   @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.supportResistanceService.pageQueryList(measureSelectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "根据主键查询", notes = "根据主键查询")
    @GetMapping(value = "/getById")
    public R<SupportResistanceDTO> detail(@ApiParam(name = "supportResistanceId", value = "主键id", required = true) @RequestParam Long supportResistanceId) {
        return R.ok(this.supportResistanceService.detail(supportResistanceId));
    }

    @ApiOperation(value = "删除工作面支架阻力测点", notes = "删除工作面支架阻力测点")
    @DeleteMapping(value = "/delete")
    public R<Boolean> delete(@ApiParam(name = "supportResistanceIds", value = "id数组", readOnly = true) @RequestParam Long[] supportResistanceIds) {
        return R.ok(this.supportResistanceService.deleteByIds(supportResistanceIds));
    }

    @ApiOperation(value = "批量启用/禁用", notes = "批量启用/禁用")
    @PutMapping(value = "/batchEnableDisable")
    public R<Object> batchEnableDisable(@ApiParam(name = "supportResistanceIds", value = "id数组", readOnly = true) @RequestParam Long[] supportResistanceIds) {
        return R.ok(this.supportResistanceService.batchEnableDisable(supportResistanceIds));
    }
}
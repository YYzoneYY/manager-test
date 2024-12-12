package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.AnchorCableStressDTO;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.service.AnchorStressService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/12/4
 * @description:
 */

@Api(tags = "锚杆(索)应力")
@RestController
@RequestMapping(value = "/anchorCableStress")
public class AnchorCableStressController {

    @Resource
    private AnchorStressService anchorStressService;

    @ApiOperation(value = "新增锚杆(索)应力测点", notes = "新增锚杆(索)应力测点")
    @PostMapping(value = "/add")
    public R<Object> addMeasure(@RequestBody AnchorCableStressDTO anchorCableStressDTO){
        return R.ok(this.anchorStressService.addMeasure(anchorCableStressDTO));
    }

    @ApiOperation(value = "修改锚杆(索)应力测点", notes = "修改锚杆(索)应力测点")
    @PutMapping(value = "/update")
    public R<Object> updateMeasure(@RequestBody @Validated({ParameterValidationUpdate.class})
                                   AnchorCableStressDTO anchorCableStressDTO){
        return R.ok(this.anchorStressService.updateMeasure(anchorCableStressDTO));
    }

    @ApiOperation(value = "根据主键查询", notes = "根据主键查询")
    @GetMapping(value = "/getById")
    public R<AnchorCableStressDTO> detail(@ApiParam(name = "anchorCableStressId", value = "主键id", required = true) @RequestParam Long anchorCableStressId){
        return R.ok(this.anchorStressService.detail(anchorCableStressId));
    }

    @ApiOperation(value = "分页查询数据列表接口", notes = "分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/pageQueryList")
    public R<Object> pageQueryList(@RequestBody MeasureSelectDTO measureSelectDTO,
                                   @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        return R.ok(this.anchorStressService.pageQueryList(measureSelectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "批量删除钻孔应力测点", notes = "批量删除钻孔应力测点")
    @DeleteMapping(value = "/delete")
    public R<Object> batchDelete(@ApiParam(name = "anchorCableStressIds", value = "主键id数组", required = true)
                                  @RequestParam Long[] anchorCableStressIds){
        return R.ok(this.anchorStressService.deleteByIds(anchorCableStressIds));
    }

    @ApiOperation(value = "批量启用/禁用", notes = "批量启用/禁用")
    @PutMapping(value = "/batchEnableDisable")
    public R<Object> batchEnableDisable(@ApiParam(name = "anchorCableStressIds", value = "主键id数组", required = true)
                                        @RequestParam Long[] anchorCableStressIds){
        return R.ok(this.anchorStressService.batchEnableDisable(anchorCableStressIds));
    }
}
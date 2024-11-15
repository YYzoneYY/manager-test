package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.ConstructUnitSelectDTO;
import com.ruoyi.system.domain.dto.ConstructionUnitDTO;
import com.ruoyi.system.domain.dto.UnitChoiceListDTO;
import com.ruoyi.system.service.ConstructionUnitService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */

@Api(tags = "施工单位管理")
@RestController
@RequestMapping(value = "/constructionUnit")
public class ConstructionUnitController {

    @Resource
    private ConstructionUnitService constructionUnitService;

    @ApiOperation(value = "新增施工单位", notes = "新增施工单位")
    @PostMapping(value = "/add")
    public R<ConstructionUnitDTO> add(@RequestBody @Validated({ParameterValidationAdd.class, ParameterValidationOther.class}) ConstructionUnitDTO constructionUnitDTO) {
        return R.ok(constructionUnitService.insertConstructionUnit(constructionUnitDTO));
    }

    @ApiOperation(value = "修改施工单位", notes = "修改施工单位")
    @PutMapping(value = "/update")
    public R<ConstructionUnitDTO> update(@RequestBody @Validated({ParameterValidationUpdate.class, ParameterValidationOther.class}) ConstructionUnitDTO constructionUnitDTO) {
        return R.ok(constructionUnitService.updateConstructionUnit(constructionUnitDTO));
    }

    @ApiOperation(value = "查询施工单位", notes = "查询施工单位")
    @GetMapping(value = "/getConstructionUnitById/{constructionUnitId}")
    public R<ConstructionUnitDTO> getConstructionUnitById(@ApiParam(name = "constructionUnitId", value = "施工单位id", required = true) @PathVariable Long constructionUnitId) {
        return R.ok(constructionUnitService.getConstructionUnitById(constructionUnitId));
    }

    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @GetMapping(value = "/pageQueryList")
    public R<TableData> pageQueryList(@RequestBody ConstructUnitSelectDTO constructUnitSelectDTO,
                                      @RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize) {
        return R.ok(constructionUnitService.pageQueryList(constructUnitSelectDTO, pageNum, pageSize));
    }

    @ApiOperation(value = "删除施工单位", notes = "删除施工单位")
    @DeleteMapping(value = "/delete")
    public R<Boolean> delete(@ApiParam(name = "constructionUnitIds", value = "施工单位id数组", readOnly = true) @RequestParam Long[] constructionUnitIds) {
        return R.ok(constructionUnitService.deleteConstructionUnit(constructionUnitIds));
    }

    @ApiOperation(value = "获取施工单位下拉列表", notes = "获取施工单位下拉列表")
    @GetMapping(value = "/getUnitChoiceList")
    public R<List<UnitChoiceListDTO>> getUnitChoiceList() {
        return R.ok(constructionUnitService.getUnitChoiceList());
    }
}
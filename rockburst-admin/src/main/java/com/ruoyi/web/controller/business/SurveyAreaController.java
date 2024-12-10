package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.SurveyAreaDTO;
import com.ruoyi.system.domain.dto.SurveySelectDTO;
import com.ruoyi.system.service.SurveyAreaService;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @title: 测区管理
 * @author: shikai
 * @date: 2024/11/12
 * @description:
 */

@Api(tags = "测区管理")
@RestController
@RequestMapping(value = "/surveyArea")
public class SurveyAreaController {

    @Resource
    private SurveyAreaService surveyAreaService;

    /**
     * 查询详情
     * @param surveyAreaId 测区id
     * @return 返回查询
     */
    @ApiOperation(value = "根据主键查询数据", notes = "根据主键查询数据")
    @GetMapping(value = "/getSurveyAreaById/{surveyAreaId}")
    public R<SurveyAreaDTO> getSurveyAreaById(@ApiParam(name = "surveyAreaId", value = "测区id", required = true) @PathVariable Long surveyAreaId){
        return R.ok(surveyAreaService.getSurveyAreaById(surveyAreaId));
    }

    /**
     * 新增测区
     * @param surveyAreaDTO 参数接收DTO
     * @return 返回结果
     */
    @ApiOperation(value = "新增测区", notes = "新增测区")
    @PostMapping(value = "/add")
    public R<SurveyAreaDTO> add(@RequestBody @Validated(value = {ParameterValidationAdd.class, ParameterValidationOther.class}) SurveyAreaDTO surveyAreaDTO){
        return R.ok(surveyAreaService.insertSurveyArea(surveyAreaDTO), "新增成功");
    }

    /**
     * 测区编辑
     * @param surveyAreaDTO 参数接收DTO
     * @return 返回结果
     */
    @ApiOperation(value = "测区修改", notes = "修改修改")
    @PutMapping(value = "/update")
    public R<SurveyAreaDTO> update(@RequestBody @Validated(value = {ParameterValidationUpdate.class, ParameterValidationOther.class}) SurveyAreaDTO surveyAreaDTO){
        return R.ok(surveyAreaService.updateSurveyArea(surveyAreaDTO), "修改成功");
    }

    /**
     * 分页查询
     * @param surveySelectDTO 查询参数DTO
     * @param pageNum 页数
     * @param pageSize 条数
     * @return 返回结果
     */
    @ApiOperation(value = "根据条件参数分页查询数据列表接口", notes = "根据条件参数分页查询数据列表接口")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/queryList")
    public R<TableData> queryList(@RequestBody SurveySelectDTO surveySelectDTO, @RequestParam(required = false) Integer pageNum, @RequestParam(required = false) Integer pageSize){
        return R.ok(surveyAreaService.pageQueryList(surveySelectDTO, pageNum, pageSize));
    }

    /**
     * 批量删除测区
     * @param surveyAreaIds 测区id
     * @return 返回
     */
    @ApiOperation(value = "批量删除测区", notes = "批量删除测区")
    @DeleteMapping(value = "/delete")
    public R<Boolean> delete(@ApiParam(name = "surveyAreaIds", value = "测区id", required = true) @RequestParam Long[] surveyAreaIds) {
        return R.ok(surveyAreaService.deleteSurveyArea(surveyAreaIds), "删除成功");
    }
}
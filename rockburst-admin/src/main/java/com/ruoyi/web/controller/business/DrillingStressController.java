package com.ruoyi.web.controller.business;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.Entity.ParameterValidationAdd;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.dto.DrillingStressDTO;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.SupportResistanceDTO;
import com.ruoyi.system.domain.dto.actual.ActualSelectDTO;
import com.ruoyi.system.service.DrillingStressService;
import com.ruoyi.system.service.MeasureActualService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/3
 * @description:
 */

@Api(tags = "钻孔应力")
@RestController
@RequestMapping(value = "/drillingStress")
public class DrillingStressController {

    @Resource
    private DrillingStressService drillingStressService;

    @Autowired
    private TokenService tokenService;

    @Resource
    private MeasureActualService measureActualService;

    @ApiOperation(value = "新增钻孔应力测点", notes = "新增钻孔应力测点")
    @PostMapping(value = "/add")
    public R<Object> addMeasure(@RequestBody DrillingStressDTO drillingStressDTO) {
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.drillingStressService.addMeasure(drillingStressDTO, mineId));
    }

    @ApiOperation(value = "修改钻孔应力测点", notes = "修改钻孔应力测点")
    @PutMapping(value = "/update")
    public R<Object> updateMeasure(@RequestBody @Validated({ParameterValidationUpdate.class})
                                       DrillingStressDTO drillingStressDTO) {
        return R.ok(this.drillingStressService.updateMeasure(drillingStressDTO));
    }

    @ApiOperation(value = "根据主键查询", notes = "根据主键查询")
    @GetMapping(value = "/getById")
    public R<DrillingStressDTO> detail(@ApiParam(name = "supportResistanceId", value = "主键id", required = true) @RequestParam Long drillingStressId) {
        return R.ok(this.drillingStressService.detail(drillingStressId));
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
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.drillingStressService.pageQueryList(measureSelectDTO, mineId, pageNum, pageSize));
    }


    @ApiOperation(value = "批量删除钻孔应力测点", notes = "批量删除钻孔应力测点")
    @DeleteMapping(value = "/delete")
    public R<Object> batchDelete(@ApiParam(name = "drillingStressIds", value = "id数组", required = true) @RequestParam  Long[] drillingStressIds) {
        return R.ok(this.drillingStressService.deleteByIds(drillingStressIds));
    }

    @ApiOperation(value = "批量启用/禁用", notes = "批量启用/禁用")
    @PutMapping(value = "/batchEnableDisable")
    public R<Object> batchEnableDisable(@ApiParam(name = "supportResistanceIds", value = "id数组", required = true) @RequestParam Long[] drillingStressIds) {
        return R.ok(this.drillingStressService.batchEnableDisable(drillingStressIds));
    }


    @ApiOperation(value = "实时数据列表", notes = "实时数据列表")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", defaultValue = "1", dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", defaultValue = "10", dataType = "Integer")
    })
    @PostMapping(value = "/actualDataList")
    public R<Object> ActualDataPage(@RequestBody ActualSelectDTO actualSelectDTO,
                                   @ApiParam(name = "pageNum", value = "页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(name = "pageSize", value = "每页数量", required = true) @RequestParam Integer pageSize) {
        List<String> sensorTypes = new ArrayList<>();
        sensorTypes.add(ConstantsInfo.DRILL_STRESS_TYPE);
        String tag = "1";
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.measureActualService.ActualDataPage(actualSelectDTO, sensorTypes, mineId, tag, pageNum, pageSize));
    }

    @ApiOperation(value = "获取曲线图数据", notes = "获取曲线图数据")
    @GetMapping(value = "/obtainLineGraph")
    public R<Object> obtainLineGraph(@ApiParam(name = "measureNum", value = "测点编号", required = true) @RequestParam String measureNum,
                                     @ApiParam(name = "range", value = "时间范围(1-30min;2-1h;3-24;4-当天;5-自定义时间)", required = true) @RequestParam String range,
                                     @RequestParam(required = false) Long startTime,
                                     @RequestParam(required = false) Long endTime) {
        List<String> sensorTypes = new ArrayList<>();
        sensorTypes.add(ConstantsInfo.DRILL_STRESS_TYPE);
        String token = tokenService.getToken(ServletUtils.getRequest());
        Long mineId = tokenService.getMineIdFromToken(token);
        return R.ok(this.measureActualService.obtainLineGraph(measureNum, range, startTime, endTime, sensorTypes, mineId));
    }
}
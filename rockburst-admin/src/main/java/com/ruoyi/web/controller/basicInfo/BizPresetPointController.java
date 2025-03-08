package com.ruoyi.web.controller.basicInfo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.dto.BizPlanPrePointDto;
import com.ruoyi.system.domain.dto.BizPresetPointDto;
import com.ruoyi.system.service.IBizPresetPointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 预设点管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-预设点")
//@Tag(description = "预设点管理Controller", name = "预设点管理Controller")
@RestController
@RequestMapping("/basicInfo/presetPoint")
public class BizPresetPointController extends BaseController
{
    @Autowired
    private IBizPresetPointService bizPresetPointService;



    /**
     *
     */
    @ApiOperation("查询预设点管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:list')")
    @GetMapping("/list")
    public R<MPage<BizPresetPoint>> list(@ParameterObject BizPresetPointDto dto, @ParameterObject Pagination pagination)
    {
        return R.ok(bizPresetPointService.selectEntityList(dto, pagination));
    }


    /**
     * 获取预设点管理详细信息
     */
    @ApiOperation("获取预设点管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:query')")
    @GetMapping(value = "/{mineId}")
    public R<BizPresetPoint> getInfo(@PathVariable("mineId") Long mineId)
    {
        return R.ok(bizPresetPointService.selectEntityById(mineId));
    }

    @ApiOperation("sssssss")
    @GetMapping(value = "ssss")
    public R sssssss(@RequestParam(required = false) Long planId,
                     @RequestParam(required = false) Long startId,
                     @RequestParam(required = false) Long tunnelId,
                     @RequestParam(required = false) Long endId,
                     @RequestParam(required = false) Double startMeter,
                     @RequestParam(required = false) Double endMeter)
    {
        BizPlanPrePointDto dto = new BizPlanPrePointDto();
        dto.setTunnelId(tunnelId).setStartPointId(startId).setStartMeter(startMeter).setEndPointId(endId).setEndMeter(endMeter);
        List<BizPlanPrePointDto> dtos = new ArrayList<>();
        dtos.add(dto);
        return R.ok(bizPresetPointService.setPlanPrePoint(planId,dtos));
    }

    /**
     * 新增预设点管理
     */
    @ApiOperation("新增预设点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:add')")
    @Log(title = "预设点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  BizPresetPointDto dto)
    {

        return R.ok(bizPresetPointService.insertEntity(dto));
    }

    /**
     * 修改预设点管理
     */
    @ApiOperation("修改预设点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:edit')")
    @Log(title = "预设点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizPresetPointDto dto)
    {
        return R.ok(bizPresetPointService.updateEntity(dto));
    }

    /**
     * 删除预设点管理
     */
    @ApiOperation("删除预设点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:remove')")
    @Log(title = "预设点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mineIds}")
    public R remove(@PathVariable Long[] presetPointIds)
    {

        UpdateWrapper<BizPresetPoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizPresetPoint::getPresetPointId, presetPointIds).set(BizPresetPoint::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizPresetPointService.update(updateWrapper));
    }


    /**
     * 删除预设点管理
     */
    @ApiOperation("删除预设点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:remove')")
    @Log(title = "预设点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{mineId}")
    public R remove(@PathVariable("mineId") Long presetPointId)
    {

        BizPresetPoint entity = new BizPresetPoint();
        entity.setPresetPointId(presetPointId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizPresetPointService.updateById(entity));
    }



}

package com.ruoyi.web.controller.basicInfo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizDangerLevel;
import com.ruoyi.system.service.IBizDangerLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 危险等级管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-危险等级")
//@Tag(description = "危险等级管理Controller", name = "危险等级管理Controller")
@RestController
@RequestMapping("/basicInfo/dangerLevel")
public class BizDangerLevelController extends BaseController
{
    @Autowired
    private IBizDangerLevelService bizDangerLevelService;



    /**
     *
     */
    @ApiOperation("查询危险等级管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerLevel:list')")
    @GetMapping("/list")
    public R<MPage<BizDangerLevel>> list(@ParameterObject BizDangerLevel dto, @ParameterObject Pagination pagination)
    {
        return R.ok(bizDangerLevelService.selectEntityList(dto, pagination));
    }


    /**
     * 获取危险等级管理详细信息
     */
    @ApiOperation("获取危险等级管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerLevel:query')")
    @GetMapping(value = "/{dangerLevelId}")
    public R<BizDangerLevel> getInfo(@PathVariable("dangerLevelId") Long dangerLevelId)
    {
        return R.ok(bizDangerLevelService.selectEntityById(dangerLevelId));
    }

    /**
     * 新增危险等级管理
     */
    @ApiOperation("新增危险等级管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerLevel:add')")
    @Log(title = "危险等级管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  @Validated(value = {GroupAdd.class})  BizDangerLevel dto)
    {
        return R.ok(bizDangerLevelService.insertEntity(dto));
    }




    /**
     * 修改危险等级管理
     */
    @ApiOperation("修改危险等级管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerLevel:edit')")
    @Log(title = "危险等级管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizDangerLevel dto)
    {

        return R.ok(bizDangerLevelService.updateEntity(dto));
    }

    /**
     * 删除危险等级管理
     */
    @ApiOperation("删除危险等级管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerLevel:remove')")
    @Log(title = "危险等级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dangerLevelIds}")
    public R remove(@PathVariable Long[] dangerLevelIds)
    {

        UpdateWrapper<BizDangerLevel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizDangerLevel::getDangerLevelId, dangerLevelIds).set(BizDangerLevel::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDangerLevelService.update(updateWrapper));
    }


    /**
     * 删除危险等级管理
     */
    @ApiOperation("删除危险等级管理")
//    @PreAuthorize("@ss.hasPermi('basicInfo:dangerLevel:remove')")
    @Log(title = "危险等级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{dangerLevelId}")
    public R remove(@PathVariable("dangerLevelId") Long dangerLevelId)
    {


        UpdateWrapper<BizDangerLevel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(BizDangerLevel::getDangerLevelId, dangerLevelId)
                .set(BizDangerLevel::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDangerLevelService.update(updateWrapper));
    }



}

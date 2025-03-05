package com.ruoyi.web.controller.map;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.MpTag;
import com.ruoyi.system.service.IMpTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 巷道帮管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "map-tag管理")
//@Tag(description = "巷道帮管理Controller", name = "巷道帮管理Controller")
@RestController
@RequestMapping("/map/tag")
public class MpTagController extends BaseController
{
    @Autowired
    private IMpTagService mpTagService;





    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询tag下拉")
    @GetMapping("/checkList")
    public R<List<MpTag>> checkList()
    {
        List<MpTag> list = mpTagService.list();
        return R.ok(list);
    }





    /**
     * 获取巷道帮管理详细信息
     */
    @ApiOperation("获取tag详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:tag:query')")
    @GetMapping(value = "/{tagId}")
    public R<MpTag> getInfo(@PathVariable("tagId") Long tagId)
    {
        return R.ok(mpTagService.getById(tagId));
    }

    /**
     * 新增巷道帮管理
     */
    @ApiOperation("新增tag")
    @PreAuthorize("@ss.hasPermi('basicInfo:tag:add')")
    @Log(title = "tag", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  MpTag dto)
    {
        return R.ok(mpTagService.save(dto));
    }

    /**
     * 修改巷道帮管理
     */
    @ApiOperation("修改tag")
    @PreAuthorize("@ss.hasPermi('basicInfo:tag:edit')")
    @Log(title = "修改tag", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody  MpTag dto)
    {

        return R.ok(mpTagService.updateById(dto));
    }

    /**
     * 删除巷道帮管理
     */
    @ApiOperation("删除tag")
    @PreAuthorize("@ss.hasPermi('basicInfo:tag:remove')")
    @Log(title = "删除tag", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tagIds}")
    public R remove(@PathVariable Long[] tagIds)
    {

        UpdateWrapper<MpTag> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(MpTag::getTagId, tagIds).set(MpTag::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(mpTagService.update(updateWrapper));
    }

}

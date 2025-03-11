package com.ruoyi.web.controller.yt;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.YtFactor;
import com.ruoyi.system.service.IYtFactorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "yt-影响因素")
@RestController
@RequestMapping("/factor/record")
public class YtFactorRecordController extends BaseController
{
    @Autowired
    private IYtFactorService ytFactorService;


    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询巷道帮管理列表")
    @PreAuthorize("@ss.hasPermi('yt:factor:list')")
    @GetMapping("/list")
    public R<MPage<YtFactor>> list(@ParameterObject YtFactor dto, @ParameterObject Pagination pagination)
    {
        MPage<YtFactor> list = ytFactorService.selectEntityList(dto,pagination);
        return R.ok(list);
    }






    /**
     * 获取巷道帮管理详细信息
     */
    @ApiOperation("获取影响区域详细信息")
    @PreAuthorize("@ss.hasPermi('yt:factor:query')")
    @GetMapping(value = "/{factorId}")
    public R<YtFactor> getInfo(@PathVariable("factorId") Long factorId)
    {
        return R.ok(ytFactorService.getById(factorId));
    }

    /**
     * 新增巷道帮管理
     */
    @ApiOperation("新增影响区域")
    @PreAuthorize("@ss.hasPermi('yt:factor:add')")
    @Log(title = "新增影响区域", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  YtFactor dto)
    {
        return R.ok(ytFactorService.save(dto));
    }

    /**
     * 修改巷道帮管理
     */
    @ApiOperation("修改影响区域")
    @PreAuthorize("@ss.hasPermi('yt:factor:edit')")
    @Log(title = "修改影响区域", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody  YtFactor dto)
    {
        return R.ok(ytFactorService.updateById(dto));
    }



    /**
     * 删除巷道帮管理
     */
    @ApiOperation("删除影响区域")
    @PreAuthorize("@ss.hasPermi('yt:factor:remove')")
    @Log(title = "删除影响区域", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{factorId}")
    public R remove(@PathVariable("factorId") Long factorId)
    {

        YtFactor entity = new YtFactor();
        entity.setFactorId(factorId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(ytFactorService.updateById(entity));
    }


}

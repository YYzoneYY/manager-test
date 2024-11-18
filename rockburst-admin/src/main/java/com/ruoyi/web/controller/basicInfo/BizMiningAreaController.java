package com.ruoyi.web.controller.basicInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizMine;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.dto.BizMiningAreaDto;
import com.ruoyi.system.domain.vo.BizMiningAreaVo;
import com.ruoyi.system.service.IBizWorkfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 采区管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api("采区管理Controller")
@RestController
@RequestMapping("/basicInfo/area")
public class BizMiningAreaController extends BaseController
{
    @Autowired
    private IBizMiningAreaService bizMiningAreaService;

    @Autowired
    private IBizWorkfaceService bizWorkfaceService;

    /**
     * 查询采区管理列表
     */
    @ApiOperation("查询采区管理列表")
    @PreAuthorize("@ss.hasPermi('system:area:list')")
    @GetMapping("/list")
    public R<MPage<BizMiningAreaVo>> list(@ParameterObject BizMiningAreaDto dto , Pagination pagination )
    {
        MPage<BizMiningAreaVo> list = bizMiningAreaService.selectBizMiningAreaList(dto,pagination);
        return R.ok(list);
    }



    /**
     * 获取采区管理详细信息
     */
    @ApiOperation("获取采区管理详细信息")
    @PreAuthorize("@ss.hasPermi('system:area:query')")
    @GetMapping(value = "/{miningAreaId}")
    public AjaxResult getInfo(@PathVariable("miningAreaId") Long miningAreaId)
    {
        return success(bizMiningAreaService.selectBizMiningAreaByMiningAreaId(miningAreaId));
    }

    /**
     * 新增采区管理
     */
    @ApiOperation("新增采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:add')")
    @Log(title = "采区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated(value = {GroupAdd.class}) BizMiningAreaDto dto)
    {
        BizMiningArea entity = new BizMiningArea();
        BeanUtil.copyProperties(dto,entity);
        return toAjax(bizMiningAreaService.insertBizMiningArea(entity));
    }

    /**
     * 修改采区管理
     */
    @ApiOperation("修改采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:edit')")
    @Log(title = "采区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizMiningAreaDto dto)
    {
        BizMiningArea entity = new BizMiningArea();
        BeanUtil.copyProperties(dto,entity);
        return toAjax(bizMiningAreaService.updateBizMiningArea(entity));
    }

    /**
     * 删除采区管理
     */
    @ApiOperation("删除采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:remove')")
    @Log(title = "采区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{miningAreaId}")
    public AjaxResult remove(@PathVariable Long miningAreaId)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizWorkface::getMiningAreaId, miningAreaId).eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = bizWorkfaceService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count > 0, "选择的采区下还有工作面");
        BizMiningArea entity = new BizMiningArea();
        entity.setMiningAreaId(miningAreaId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return toAjax(bizMiningAreaService.updateById(entity));
    }
    /**
     * 删除采区管理
     */
    @ApiOperation("删除采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:remove')")
    @Log(title = "采区管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{miningAreaIds}")
    public AjaxResult remove(@PathVariable Long[] miningAreaIds)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(BizWorkface::getMiningAreaId, miningAreaIds).eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = bizWorkfaceService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count > 0, "选择的采区下还有工作面");
        UpdateWrapper<BizMiningArea> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizMiningArea::getMiningAreaId, miningAreaIds)
                .set(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return toAjax(bizMiningAreaService.update(updateWrapper));    }
}

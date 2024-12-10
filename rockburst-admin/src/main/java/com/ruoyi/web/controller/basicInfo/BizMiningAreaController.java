package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.dto.BizMiningAreaDto;
import com.ruoyi.system.domain.vo.BizMiningAreaVo;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.system.service.IBizWorkfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采区管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-采区管理")
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

    @ApiOperation("下拉全部列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<BizMiningArea>> checkList(  @RequestParam(value = "状态合集", required = false) Long[] statuss,
                                              @RequestParam(value = "矿井id集合", required = false) Long[] mineIds)
    {
        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(statuss != null && statuss.length > 0, BizMiningArea::getStatus, statuss)
                .in(mineIds != null && mineIds.length > 0, BizMiningArea::getMineId, mineIds)
                .eq(BizMiningArea::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizMiningArea> list = bizMiningAreaService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }



    /**
     * 获取采区管理详细信息
     */
    @ApiOperation("获取采区管理详细信息")
    @PreAuthorize("@ss.hasPermi('system:area:query')")
    @GetMapping(value = "/{miningAreaId}")
    public R<BizMiningAreaVo> getInfo(@PathVariable("miningAreaId") Long miningAreaId)
    {
        return R.ok(bizMiningAreaService.selectBizMiningAreaByMiningAreaId(miningAreaId));
    }

    /**
     * 新增采区管理
     */
    @ApiOperation("新增采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:add')")
    @Log(title = "采区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(value = {GroupAdd.class}) BizMiningAreaDto dto)
    {
        BizMiningArea entity = new BizMiningArea();
        BeanUtil.copyProperties(dto,entity);
        return R.ok(bizMiningAreaService.insertBizMiningArea(entity));
    }

    /**
     * 修改采区管理
     */
    @ApiOperation("修改采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:edit')")
    @Log(title = "采区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizMiningAreaDto dto)
    {
        BizMiningArea entity = new BizMiningArea();
        BeanUtil.copyProperties(dto,entity);
        return R.ok(bizMiningAreaService.updateBizMiningArea(entity));
    }

    /**
     * 删除采区管理
     */
//    @Anonymous
    @ApiOperation("删除采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:remove')")
    @Log(title = "采区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{miningAreaId}")
    public R remove(@PathVariable Long miningAreaId)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizWorkface::getMiningAreaId, miningAreaId).eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = bizWorkfaceService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count == 0, "选择的采区下还有工作面");
        BizMiningArea entity = new BizMiningArea();
        entity.setMiningAreaId(miningAreaId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizMiningAreaService.updateById(entity));
    }
    /**
     * 删除采区管理
     */
    @ApiOperation("删除采区管理")
    @PreAuthorize("@ss.hasPermi('system:area:remove')")
    @Log(title = "采区管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{miningAreaIds}")
    public R remove(@PathVariable Long[] miningAreaIds)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(BizWorkface::getMiningAreaId, miningAreaIds).eq(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = bizWorkfaceService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count == 0, "选择的采区下还有工作面");
        UpdateWrapper<BizMiningArea> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizMiningArea::getMiningAreaId, miningAreaIds)
                .set(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok(bizMiningAreaService.update(updateWrapper));    }
}

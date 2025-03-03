package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.dto.BizTunnelBarDto;
import com.ruoyi.system.domain.vo.BizTunnelBarVo;
import com.ruoyi.system.service.IBizTunnelBarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 巷道帮管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-巷道帮管理")
//@Tag(description = "巷道帮管理Controller", name = "巷道帮管理Controller")
@RestController
@RequestMapping("/basicInfo/bar")
public class BizTunnelBarController extends BaseController
{
    @Autowired
    private IBizTunnelBarService bizTunnelBarService;


    @ApiOperation("调整比例")
//    @PreAuthorize("@ss.hasPermi('basicInfo:bar:list')")
    @PostMapping("/barRange")
    public R barRange(@RequestParam(required = false) String startLat,
                      @RequestParam(required = false) String startLon,
                      @RequestParam(required = false) String endLat,
                      @RequestParam(required = false) String endLon,
                      @RequestParam(required = false) String drillrange)
    {
        BigDecimal startLatBd = new BigDecimal(startLat);
        BigDecimal startLonBd = new BigDecimal(startLon);
        BigDecimal endLatBd = new BigDecimal(endLat);
        BigDecimal endLonBd = new BigDecimal(endLon);
        BigDecimal drillrangeBd = new BigDecimal(drillrange);
        BigDecimal latMove = startLatBd.subtract(endLatBd).abs();
        BigDecimal lonMove = startLonBd.subtract(endLonBd).abs();

        // dx^2
        BigDecimal dxSquare = latMove.pow(2);
        // dy^2
        BigDecimal dySquare = lonMove.pow(2);
        // dx^2 + dy^2
        BigDecimal sum = dxSquare.add(dySquare);

        BigDecimal lonlatrange = new BigDecimal(Math.sqrt(sum.doubleValue())).abs().setScale(10, RoundingMode.HALF_DOWN);

        BigDecimal bili = lonlatrange.divide(drillrangeBd, 10, RoundingMode.HALF_DOWN);

        List<BizTunnelBar> bizTunnelBars =  bizTunnelBarService.list();
        for (BizTunnelBar bizTunnelBar : bizTunnelBars) {
            UpdateWrapper<BizTunnelBar> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(BizTunnelBar::getDirectRange,bili).eq(BizTunnelBar::getBarId,bizTunnelBar.getBarId()).eq(BizTunnelBar::getDelFlag,BizBaseConstant.DELFLAG_N);
            bizTunnelBarService.update(updateWrapper);
        }

        return R.ok();
    }

    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询巷道帮管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:list')")
    @GetMapping("/list")
    public R<MPage<BizTunnelBarVo>> list(@ParameterObject BizTunnelBarDto dto, @ParameterObject Pagination pagination)
    {
        MPage<BizTunnelBarVo> list = bizTunnelBarService.selectEntityList(dto,pagination);
        return R.ok(list);
    }

    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询巷道帮下拉")
    @GetMapping("/checkList")
    public R<List<BizTunnelBar>> checkList(@ParameterObject BizTunnelBarDto dto)
    {
        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StrUtil.isNotEmpty(dto.getType()), BizTunnelBar::getType,dto.getType())
                .eq(StrUtil.isNotEmpty(dto.getBarName()),BizTunnelBar::getBarName,dto.getBarName())
                .eq(dto.getTunnelId() != null,BizTunnelBar::getTunnelId, dto.getTunnelId());
        List<BizTunnelBar> list = bizTunnelBarService.list(queryWrapper);
        return R.ok(list);
    }





    /**
     * 获取巷道帮管理详细信息
     */
    @ApiOperation("获取巷道帮管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:query')")
    @GetMapping(value = "/{barId}")
    public R<BizTunnelBar> getInfo(@PathVariable("barId") Long barId)
    {
        return R.ok(bizTunnelBarService.selectEntityById(barId));
    }

    /**
     * 新增巷道帮管理
     */
    @ApiOperation("新增巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:add')")
    @Log(title = "巷道帮管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  BizTunnelBarDto dto)
    {
        return R.ok(bizTunnelBarService.insertEntity(dto));
    }

    /**
     * 修改巷道帮管理
     */
    @ApiOperation("修改巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:edit')")
    @Log(title = "巷道帮管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizTunnelBarDto dto)
    {

        return R.ok(bizTunnelBarService.updateEntity(dto));
    }

    /**
     * 删除巷道帮管理
     */
    @ApiOperation("删除巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:remove')")
    @Log(title = "巷道帮管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{barIds}")
    public R remove(@PathVariable Long[] barIds)
    {
//        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().in(BizTunnelBar::getBarId, barIds).eq(BizTunnelBar::getDelFlag, BizBaseConstant.DELFLAG_N);
//        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
//        Assert.isTrue(count == 0, "选择的巷道帮下还有采区");
        UpdateWrapper<BizTunnelBar> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizTunnelBar::getBarId, barIds).set(BizTunnelBar::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTunnelBarService.update(updateWrapper));
    }


    /**
     * 删除巷道帮管理
     */
    @ApiOperation("删除巷道帮管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:bar:remove')")
    @Log(title = "巷道帮管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{barId}")
    public R remove(@PathVariable("barId") Long barId)
    {
//        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(BizMiningArea::getbarId, barId).eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N);
//        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
//        Assert.isTrue(count == 0, "选择的巷道帮下还有采区");
        BizTunnelBar entity = new BizTunnelBar();
        entity.setBarId(barId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTunnelBarService.updateById(entity));
    }


}

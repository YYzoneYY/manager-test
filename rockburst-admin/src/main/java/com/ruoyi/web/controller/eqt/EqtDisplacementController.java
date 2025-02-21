package com.ruoyi.web.controller.eqt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.EqtDisplacement;
import com.ruoyi.system.domain.dto.EqtDisplacementDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.service.IEqtDisplacementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 矿井管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "eqt-巷道表面位移")
@RestController
@RequestMapping("/eqt/displacement")
public class EqtDisplacementController extends BaseController
{
    @Autowired
    private IEqtDisplacementService eqtDisplacementService;

    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询巷道表面位移列表")
    @PreAuthorize("@ss.hasPermi('eqt:displacement:list')")
    @GetMapping("/list")
    public R<MPage<EqtDisplacement>> list(@ParameterObject EqtSearchDto dto, @ParameterObject Pagination pagination)
    {
        MPage<EqtDisplacement> list = eqtDisplacementService.selectPageList(dto,pagination);
        return R.ok(list);
    }


    @ApiOperation("下拉全部巷道表面位移列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<EqtDisplacement>> checkList(@RequestParam( required = false) Long[] statuss)
    {
        QueryWrapper<EqtDisplacement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(EqtDisplacement::getStatus, statuss)
                .eq(EqtDisplacement::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<EqtDisplacement> list = eqtDisplacementService.list(queryWrapper);
        return R.ok(list);
    }





    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取巷道表面位移详细信息")
    @PreAuthorize("@ss.hasPermi('eqt:displacement:query')")
    @GetMapping(value = "/{displacement}")
    public R<EqtDisplacement> getInfo(@PathVariable("displacement") Long displacement)
    {
        return R.ok(eqtDisplacementService.selectDeepById(displacement));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增巷道表面位移")
    @PreAuthorize("@ss.hasPermi('eqt:displacement:add')")
    @Log(title = "巷道表面位移", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody EqtDisplacementDto dto)
    {
        return R.ok(eqtDisplacementService.insertEntity(dto));
    }

    /**
     * 修改矿井管理
     */
    @ApiOperation("修改巷道表面位移")
    @PreAuthorize("@ss.hasPermi('eqt:displacement:edit')")
    @Log(title = "巷道表面位移", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) EqtDisplacementDto dto)
    {
        return R.ok(eqtDisplacementService.updateMById(dto));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除巷道表面位移")
    @PreAuthorize("@ss.hasPermi('eqt:displacement:remove')")
    @Log(title = "巷道表面位移", businessType = BusinessType.DELETE)
    @DeleteMapping("/{displacements}")
    public R remove(@PathVariable Long[] displacements)
    {
//        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().in(BizMiningArea::getMineId, mineIds).eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N);
//        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
//        Assert.isTrue(count == 0, "选择的矿井下还有采区");

        return R.ok(eqtDisplacementService.deleteMByIds(displacements));
    }


    /**
     * 删除矿井管理
     */
    @ApiOperation("删除巷道表面位移")
    @PreAuthorize("@ss.hasPermi('eqt:displacement:remove')")
    @Log(title = "巷道表面位移", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{displacement}")
    public R remove(@PathVariable("displacement") Long displacement)
    {
//        QueryWrapper<BizMiningArea> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(BizMiningArea::getMineId, mineId).eq(BizMiningArea::getDelFlag, BizBaseConstant.DELFLAG_N);
//        Long count = bizMiningAreaService.getBaseMapper().selectCount(queryWrapper);
//        Assert.isTrue(count == 0, "选择的矿井下还有采区");

        return R.ok(eqtDisplacementService.deleteMById(displacement));
    }
}

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
import com.ruoyi.system.domain.EqtEme;
import com.ruoyi.system.domain.dto.EqtEmeDto;
import com.ruoyi.system.domain.dto.EqtSearchDto;
import com.ruoyi.system.service.IEqtEmeService;
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
//@Api(tags = "eqt-电磁辐射")
////@Tag(description = "矿井管理Controller", name = "矿井管理Controller")
//@RestController
//@RequestMapping("/eqt/eme")
public class EqtEmeController extends BaseController
{
    @Autowired
    private IEqtEmeService eqtEmeService;



    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询电磁辐射列表")
    @PreAuthorize("@ss.hasPermi('eqt:eme:list')")
    @GetMapping("/list")
    public R<MPage<EqtEme>> list(@ParameterObject EqtSearchDto dto, @ParameterObject Pagination pagination)
    {
        return R.ok(eqtEmeService.selectPageList(dto, pagination));
    }


    @ApiOperation("下拉全部电磁辐射列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:eme:list')")
    @GetMapping("/checkList")
    public R<List<EqtEme>> checkList(@RequestParam( required = false) Long[] statuss)
    {
        QueryWrapper<EqtEme> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(statuss.length > 0, EqtEme::getStatus, statuss)
                .eq(EqtEme::getDelFlag, BizBaseConstant.DELFLAG_N);
        List<EqtEme> list = eqtEmeService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }





    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取电磁辐射详细信息")
    @PreAuthorize("@ss.hasPermi('eqt:eme:query')")
    @GetMapping(value = "/{emeId}")
    public R<EqtEme> getInfo(@PathVariable("emeId") Long emeId)
    {
        return R.ok(eqtEmeService.selectDeepById(emeId));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增电磁辐射管理")
    @PreAuthorize("@ss.hasPermi('eqt:eme:add')")
    @Log(title = "电磁辐射", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody EqtEmeDto dto)
    {
        return R.ok(eqtEmeService.insertEntity(dto));
    }

    /**
     * 修改矿井管理
     */
    @ApiOperation("修改电磁辐射管理")
    @PreAuthorize("@ss.hasPermi('eqt:eme:edit')")
    @Log(title = "电磁辐射", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) EqtEmeDto dto)
    {
        return R.ok(eqtEmeService.updateMById(dto));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除电磁辐射管理")
    @PreAuthorize("@ss.hasPermi('eqt:eme:remove')")
    @Log(title = "电磁辐射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{emeIds}")
    public R remove(@PathVariable Long[] emeIds)
    {
        return R.ok(eqtEmeService.deleteMByIds(emeIds));
    }


    /**
     * 删除矿井管理
     */
    @ApiOperation("删除电磁辐射")
    @PreAuthorize("@ss.hasPermi('eqt:eme:remove')")
    @Log(title = "电磁辐射", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{emeId}")
    public R remove(@PathVariable("emeId") Long emeId)
    {
        return R.ok(eqtEmeService.deleteMById(emeId));
    }
}

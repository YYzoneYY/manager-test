package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.dto.BizTravePointDto;
import com.ruoyi.system.service.IBizMineService;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.system.service.IBizTravePointService;
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
@Api(tags = "basic-导线点管理")
@RestController
@RequestMapping("/basicInfo/point")
public class BiztravePointController extends BaseController
{
    @Autowired
    private IBizMineService bizMineService;

    @Autowired
    private IBizTravePointService bizTravePointService;

    @Autowired
    private IBizMiningAreaService   bizMiningAreaService;

    /**
     * 查询矿井管理列表
     */
    @ApiOperation("查询矿井管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/list")
    public R<MPage<BizTravePoint>> list(@ParameterObject BizTravePointDto dto, @ParameterObject Pagination pagination)
    {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<BizTravePoint>();
        queryWrapper.lambda()
                .like(StrUtil.isNotEmpty(dto.getAxisx()),BizTravePoint::getAxisx,dto.getAxisx())
                .like(StrUtil.isNotEmpty(dto.getAxisy()),BizTravePoint::getAxisx,dto.getAxisy())
                .like(StrUtil.isNotEmpty(dto.getAxisz()),BizTravePoint::getAxisx,dto.getAxisz())
                .like(StrUtil.isNotEmpty( dto.getPointName()), BizTravePoint::getPointName, dto.getPointName())
                .eq(dto.getStatus() != null , BizTravePoint::getStatus,dto.getStatus())
                .eq(BizTravePoint::getDelFlag, BizBaseConstant.DELFLAG_N);
        IPage<BizTravePoint> list = bizTravePointService.getBaseMapper().selectPage(pagination,queryWrapper);
        return R.ok(new MPage<>(list));
    }


    @ApiOperation("下拉全部矿列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<BizTravePoint>> checkList(@RequestParam(value = "状态合集", required = false) Long[] statuss,
                                            @RequestParam(value = "工作面合集", required = false) Long[] workfaceIds)
    {
        QueryWrapper<BizTravePoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .in(statuss != null && statuss.length > 0, BizTravePoint::getStatus, statuss)
                .in(workfaceIds != null && workfaceIds.length > 0, BizTravePoint::getWorkfaceId, workfaceIds)
                .eq(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizTravePoint> list = bizTravePointService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }



    /**
     * 获取矿井管理详细信息
     */
    @ApiOperation("获取导线点管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:query')")
    @GetMapping(value = "/{mineId}")
    public R<BizTravePoint> getInfo(@PathVariable("pointId") Long pointId)
    {
        return R.ok(bizTravePointService.getBaseMapper().selectById(pointId));
    }

    /**
     * 新增矿井管理
     */
    @ApiOperation("新增导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:add')")
    @Log(title = "新增导线点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(value = {GroupAdd.class}) BizTravePointDto dto)
    {
        BizTravePoint entity = new BizTravePoint();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizTravePointService.getBaseMapper().insert(entity));
    }

    /**
     * 修改矿井管理
     */
    @ApiOperation("修改导线点管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:edit')")
    @Log(title = "修改导线点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizTravePointDto dto)
    {
        BizTravePoint entity = new BizTravePoint();
        BeanUtil.copyProperties(dto, entity);
        return R.ok(bizTravePointService.updateById(entity));
    }

    /**
     * 删除矿井管理
     */
    @ApiOperation("删除导线点管理批量")
    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "删除导线点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{pointIds}")
    public R remove(@PathVariable Long[] pointIds)
    {
        UpdateWrapper<BizTravePoint> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizTravePoint::getPointId, pointIds).set(BizTravePoint::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTravePointService.update(updateWrapper));
    }


    /**
     * 删除矿井管理
     */
    @ApiOperation("删除导线点管理")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:remove')")
    @Log(title = "删除导线点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{pointId}")
    public R remove(@PathVariable("pointId") Long pointId)
    {
        BizTravePoint entity = new BizTravePoint();
        entity.setPointId(pointId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizTravePointService.updateById(entity));
    }
}

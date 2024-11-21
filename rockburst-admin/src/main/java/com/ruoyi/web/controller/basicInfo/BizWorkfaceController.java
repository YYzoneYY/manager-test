package com.ruoyi.web.controller.basicInfo;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.Entity.SurveyAreaEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizWorkfaceDto;
import com.ruoyi.system.domain.vo.BizWorkfaceVo;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.SurveyAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.context.annotation.Bean;
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
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.service.IBizWorkfaceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 工作面管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api("工作面管理Controller")
@RestController
@RequestMapping("/basicInfo/workface")
public class BizWorkfaceController extends BaseController
{
    @Autowired
    private IBizWorkfaceService bizWorkfaceService;

    @Autowired
    private SurveyAreaService surveyAreaService;

    @Autowired
    private TunnelMapper tunnelMapper;

    /**
     * 查询工作面管理列表
     */
    @ApiOperation("查询工作面管理列表")
    @PreAuthorize("@ss.hasPermi('system:workface:list')")
    @GetMapping("/list")
    public R<MPage<BizWorkfaceVo>> list(@ParameterObject BizWorkfaceDto dto, @ParameterObject Pagination pagination)
    {
        MPage<BizWorkfaceVo> list = bizWorkfaceService.selectBizWorkfaceList(dto,pagination);
        return R.ok(list);
    }



    /**
     * 获取工作面管理详细信息
     */
    @ApiOperation("获取工作面管理详细信息")
    @PreAuthorize("@ss.hasPermi('system:workface:query')")
    @GetMapping(value = "/{workfaceId}")
    public AjaxResult getInfo(@PathVariable("workfaceId") Long workfaceId)
    {
        return success(bizWorkfaceService.selectBizWorkfaceByWorkfaceId(workfaceId));
    }

    /**
     * 新增工作面管理
     */
    @ApiOperation("新增工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:add')")
    @Log(title = "工作面管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Validated(value = {GroupAdd.class}) BizWorkfaceDto dto)
    {
        BizWorkface entity = new BizWorkface();
        BeanUtil.copyProperties(dto, entity);
        return toAjax(bizWorkfaceService.insertBizWorkface(entity));
    }

    /**
     * 修改工作面管理
     */
    @ApiOperation("修改工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:edit')")
    @Log(title = "工作面管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizWorkface dto)
    {
        BizWorkface entity = new BizWorkface();
        BeanUtil.copyProperties(dto, entity);
        return toAjax(bizWorkfaceService.updateById(entity));
    }

    /**
     * 删除工作面管理
     */
    @ApiOperation("删除工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:remove')")
    @Log(title = "工作面管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{workfaceId}")
    public AjaxResult removeOne(@PathVariable Long workfaceId)
    {
        QueryWrapper<SurveyAreaEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SurveyAreaEntity::getWorkFaceId, workfaceId)
                .eq(SurveyAreaEntity::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = surveyAreaService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count > 0, "选择的工作面下还有采区");

        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda().eq(TunnelEntity::getWorkFaceId, workfaceId);
//                .eq(TunnelEntity::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long tunnelCount = tunnelMapper.selectCount(tunnelQueryWrapper);
        Assert.isTrue(tunnelCount > 0, "选择的工作面下还有巷道");
        BizWorkface entity = new BizWorkface();
        entity.setWorkfaceId(workfaceId).setDelFlag(BizBaseConstant.DELFLAG_N);
        return toAjax( bizWorkfaceService.updateById(entity));
    }

    /**
     * 删除工作面管理
     */
    @ApiOperation("删除工作面管理")
    @PreAuthorize("@ss.hasPermi('system:workface:remove')")
    @Log(title = "工作面管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workfaceIds}")
    public AjaxResult remove(@PathVariable Long[] workfaceIds)
    {

        QueryWrapper<SurveyAreaEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SurveyAreaEntity::getWorkFaceId, workfaceIds)
                .eq(SurveyAreaEntity::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long count = surveyAreaService.getBaseMapper().selectCount(queryWrapper);
        Assert.isTrue(count > 0, "选择的工作面下还有采区");
        //todo 巷道 还没有基础接口
        Assert.isTrue(count > 0, "选择的工作面下还有巷道");
        UpdateWrapper<BizWorkface> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizWorkface::getWorkfaceId, workfaceIds)
                .set(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return toAjax( bizWorkfaceService.update(updateWrapper));
    }
}

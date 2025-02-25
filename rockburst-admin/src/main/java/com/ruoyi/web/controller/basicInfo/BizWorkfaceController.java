package com.ruoyi.web.controller.basicInfo;

import cn.hutool.core.bean.BeanUtil;
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
import com.ruoyi.system.constant.GroupAdd;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizMiningArea;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizWorkfaceDto;
import com.ruoyi.system.domain.dto.BizWorkfaceSchemeDto;
import com.ruoyi.system.domain.dto.BizWorkfaceSvg;
import com.ruoyi.system.domain.vo.BizWorkfaceVo;
import com.ruoyi.system.domain.vo.JsonVo;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizMiningAreaService;
import com.ruoyi.system.service.IBizWorkfaceService;
import com.ruoyi.system.service.SurveyAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 工作面管理Controller
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-工作面管理")
@RestController
@RequestMapping("/basicInfo/workface")
public class BizWorkfaceController extends BaseController
{
    @Autowired
    private IBizWorkfaceService bizWorkfaceService;

    @Autowired
    private IBizMiningAreaService bizMiningAreaService;

    @Autowired
    private SurveyAreaService surveyAreaService;

    @Autowired
    private TunnelMapper tunnelMapper;


    @ApiOperation("获取工作面all")
    @Log(title = "获取工作面all", businessType = BusinessType.INSERT)
    @PostMapping("/workfaceAll")
    public R<?> getWorkfaceListAll()
    {
        List<JsonVo> cos = bizWorkfaceService.selectWorkfacejilianList();
        return R.ok(cos);
    }

    /**
     * 查询工作面管理列表
     */
    @ApiOperation("查询工作面管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:list')")
    @GetMapping("/list")
    public R<MPage<BizWorkfaceVo>> list(@ParameterObject BizWorkfaceDto dto, @ParameterObject Pagination pagination)
    {
        MPage<BizWorkfaceVo> list = bizWorkfaceService.selectBizWorkfaceList(dto,pagination);
        return R.ok(list);
    }

    @ApiOperation("下拉工作面列表")
//    @PreAuthorize("@ss.hasPermi('basicInfo:mine:list')")
    @GetMapping("/checkList")
    public R<List<BizWorkface>> checkList(@RequestParam( required = false) Long[] statuss,
                                          @RequestParam( required = false) Long[] mineIds,
                                          @RequestParam( required = false) Long[] miningAreaIds)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName)
                .in(statuss != null && statuss.length > 0, BizWorkface::getStatus, statuss)
                .in(mineIds != null && mineIds.length > 0, BizWorkface::getMineId, mineIds)
                .in(miningAreaIds != null && miningAreaIds.length > 0, BizWorkface::getMiningAreaId, miningAreaIds)
                .eq(BizWorkface::getDelFlag,BizBaseConstant.DELFLAG_N);
        List<BizWorkface> list = bizWorkfaceService.getBaseMapper().selectList(queryWrapper);
        return R.ok(list);
    }

    /**
     * 获取工作面管理详细信息
     */
    @ApiOperation("获取工作面管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:query')")
    @GetMapping(value = "/{workfaceId}")
    public R getInfo(@PathVariable("workfaceId") Long workfaceId)
    {
        return R.ok(bizWorkfaceService.selectBizWorkfaceByWorkfaceId(workfaceId));
    }

    /**
     * 新增工作面管理
     */
    @ApiOperation("新增工作面管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:add')")
    @Log(title = "工作面管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(value = {GroupAdd.class}) BizWorkfaceDto dto)
    {
        BizWorkface entity = new BizWorkface();
        BeanUtil.copyProperties(dto, entity);
        BizMiningArea area = bizMiningAreaService.getBaseMapper().selectById(entity.getMiningAreaId());
        if(area != null){
            entity.setMineId(area.getMineId());
        }
        return R.ok(bizWorkfaceService.insertBizWorkface(entity));
    }

    @ApiOperation("添加工作面规划")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:schemeEdit')")
    @Log(title = "添加工作面规划", businessType = BusinessType.UPDATE)
    @PostMapping("/schemeAdd")
    public R schemeUpdate(@RequestBody  BizWorkfaceSchemeDto dto)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName,BizWorkface::getScheme)
                        .apply("FIND_IN_SET({0}, scheme)", dto.getScheme());
        List<BizWorkface> workfaces = bizWorkfaceService.list(queryWrapper);
        for (BizWorkface workface : workfaces) {
            List<String> schemeList = new ArrayList<>(Arrays.asList(workface.getScheme().split(",")));
            schemeList.remove(dto.getScheme());
            if(schemeList == null || schemeList.size() == 0){
                UpdateWrapper<BizWorkface> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda().set(BizWorkface::getScheme,null).eq(BizWorkface::getWorkfaceId,workface.getWorkfaceId());
                bizWorkfaceService.update(updateWrapper);
            }else{
                workface.setScheme(String.join(",", schemeList));
                bizWorkfaceService.updateById(workface);
            }
        }

        QueryWrapper<BizWorkface> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName,BizWorkface::getScheme)
                .in(BizWorkface::getWorkfaceId,dto.getWorkfaceIds());
        List<BizWorkface> workfaces1 = bizWorkfaceService.list(queryWrapper1);
        for (BizWorkface workface : workfaces1) {
            if(StrUtil.isEmpty(workface.getScheme())){
                workface.setScheme(dto.getScheme());
                bizWorkfaceService.updateById(workface);
                continue;
            }
            List<String> schemeList = new ArrayList<>(Arrays.asList(workface.getScheme().split(",")));
            if(!schemeList.contains(dto.getScheme())){
                schemeList.add(dto.getScheme());
                workface.setScheme(String.join(",", schemeList));
                bizWorkfaceService.updateById(workface);
            }
        }
        return R.ok();
    }


    @ApiOperation("删除工作面规划")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:schemeEdit')")
//    @Log(title = "删除工作面规划", businessType = BusinessType.UPDATE)
    @PostMapping("/schemeRemove")
    public R schemeRemove(@RequestBody  BizWorkfaceSchemeDto dto)
    {
        QueryWrapper<BizWorkface> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().select(BizWorkface::getWorkfaceId,BizWorkface::getWorkfaceName,BizWorkface::getScheme)
                .in(BizWorkface::getWorkfaceId,dto.getWorkfaceIds());
        List<BizWorkface> workfaces = bizWorkfaceService.list(queryWrapper);
        for (BizWorkface workface : workfaces) {
            if(StrUtil.isEmpty(workface.getScheme())){
                continue;
            }
            List<String> schemeList = new ArrayList<>(Arrays.asList(workface.getScheme().split(",")));
            if(schemeList.contains(dto.getScheme())){

                schemeList.remove(dto.getScheme());
                schemeList.remove("");
                if(schemeList == null || schemeList.size() == 0){
                    UpdateWrapper<BizWorkface> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().set(BizWorkface::getScheme,null).eq(BizWorkface::getWorkfaceId,workface.getWorkfaceId());
                    bizWorkfaceService.update(updateWrapper);

                }else {
                    workface.setScheme(String.join(",", schemeList));
                    bizWorkfaceService.updateById(workface);
                }

            }
        }
        return R.ok();
    }


    /**
     * 修改工作面管理
     */
    @ApiOperation("修改工作面管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:edit')")
    @Log(title = "工作面管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizWorkface dto)
    {
        BizWorkface entity = new BizWorkface();
        BeanUtil.copyProperties(dto, entity);
        BizMiningArea area = bizMiningAreaService.getBaseMapper().selectById(entity.getMiningAreaId());
        if(area != null){
            entity.setMineId(area.getMineId());
        }
        return R.ok(bizWorkfaceService.updateById(entity));
    }

    @ApiOperation("修改工作面地图")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:editSvg')")
    @Log(title = "修改工作面地图", businessType = BusinessType.UPDATE)
    @PutMapping("/editSvg")
    public R editSvg(@RequestBody BizWorkfaceSvg svg)
    {
        BizWorkface entity = new BizWorkface();
        BeanUtil.copyProperties(svg, entity);
        return R.ok(bizWorkfaceService.updateById(entity));
    }

    /**
     * 删除工作面管理
     */
    @ApiOperation("删除工作面管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:remove')")
    @Log(title = "工作面管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{workfaceId}")
    public R removeOne(@PathVariable Long workfaceId)
    {


        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda().eq(TunnelEntity::getWorkFaceId, workfaceId);
//                .eq(TunnelEntity::getDelFlag, BizBaseConstant.DELFLAG_N);
        Long tunnelCount = tunnelMapper.selectCount(tunnelQueryWrapper);
        Assert.isTrue(tunnelCount == 0, "选择的工作面下还有巷道");
        BizWorkface entity = new BizWorkface();
        entity.setWorkfaceId(workfaceId).setDelFlag(BizBaseConstant.DELFLAG_N);
        return R.ok( bizWorkfaceService.updateById(entity));
    }

    /**
     * 删除工作面管理
     */
    @ApiOperation("删除工作面管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:workface:remove')")
    @Log(title = "工作面管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workfaceIds}")
    public R remove(@PathVariable Long[] workfaceIds)
    {
        QueryWrapper<TunnelEntity> tunnelQueryWrapper = new QueryWrapper<>();
        tunnelQueryWrapper.lambda().in(TunnelEntity::getWorkFaceId, workfaceIds);
        long count = tunnelMapper.selectCount(tunnelQueryWrapper);

        //todo 巷道 还没有基础接口
        Assert.isTrue(count == 0, "选择的工作面下还有巷道");
        UpdateWrapper<BizWorkface> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizWorkface::getWorkfaceId, workfaceIds)
                .set(BizWorkface::getDelFlag, BizBaseConstant.DELFLAG_Y);
        return R.ok( bizWorkfaceService.update(updateWrapper));
    }
}

package com.ruoyi.web.controller.basicInfo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.service.IBizDangerAreaService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.TunnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 危险区管理Controller
 *
 * @author ruoyi
 * @date 2024-11-11
 */
@Api(tags = "basic-危险区")
//@Tag(description = "危险区管理Controller", name = "危险区管理Controller")
@RestController
@RequestMapping("/basicInfo/dangerArea")
public class BizDangerAreaController extends BaseController
{
    @Autowired
    private IBizDangerAreaService bizDangerAreaService;

    @Autowired
    private TunnelService tunnelService;

    @Autowired
    private IBizTravePointService bizTravePointService;



    /**
     *
     */
    @ApiOperation("查询危险区管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:list')")
    @GetMapping("/list")
    public R<MPage<BizDangerArea>> list(@ParameterObject BizDangerAreaDto dto, @ParameterObject Pagination pagination)
    {
        return R.ok(bizDangerAreaService.selectEntityList(dto, pagination));
    }


    /**
     * 获取危险区管理详细信息
     */
    @ApiOperation("获取危险区管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:query')")
    @GetMapping(value = "/{mineId}")
    public R<BizDangerArea> getInfo(@PathVariable("mineId") Long mineId)
    {
        return R.ok(bizDangerAreaService.selectEntityById(mineId));
    }

    /**
     * 新增危险区管理
     */
    @ApiOperation("新增危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:add')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody  BizDangerAreaDto dto)
    {

        return R.ok(bizDangerAreaService.insertEntity(dto));
    }


    @ApiOperation("生成预设点")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:sss')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping("addpre")
    public R addpre(@PathVariable Long workfaceId)
    {

        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TunnelEntity::getWorkFaceId, workfaceId);
        List<TunnelEntity> tunnelEntities  =  tunnelService.list(queryWrapper);
        for (TunnelEntity tunnelEntity : tunnelEntities) {
            List<BizDangerArea> areas = getAreaSort(tunnelEntity.getTunnelId());
            if(areas != null && areas.size() > 0){

            }
        }
        //循环所有危险区 从第一个危险区开始 每个巷道 从1 开始

        return R.ok(bizDangerAreaService.initPresetPoint(workfaceId));
    }



    public List<BizDangerArea> getAreaSort(Long tunnelId){
        QueryWrapper<BizDangerArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizDangerArea::getTunnelId,tunnelId);
        List<BizDangerArea> list = bizDangerAreaService.list(queryWrapper);
        list.stream().sorted(Comparator.comparing(BizDangerArea::getNo)).collect(Collectors.toList());
        return list;
    }

    public void initAreaPrePoint(Long areaId,Long tunnelId){
        //获取当前区域
        //获取当前区域的起始导线点 加 距离  ( 规定规则, 一定为 导线点 前 n 米)
        BizDangerArea dangerArea =  bizDangerAreaService.getByIdDeep(areaId);
        BizPresetPoint point = bizTravePointService.getPresetPoint(dangerArea.getStartPointId(),dangerArea.getStartMeter(),dangerArea.getDangerLevel().getSpaced());
        point.setDangerAreaId(areaId).setTunnelId(tunnelId);
        bizTravePointService.getNextPoint(point.getPointId());

    }

    @Anonymous
    @ApiOperation("dddd")
//    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:edit')")
    @Log(title = "dddd", businessType = BusinessType.UPDATE)
    @GetMapping("getInPoint")
    public R getInPoint(Long startPointId,Double startMeter,Long endPointId,Double endMeter)
    {
        return R.ok(bizTravePointService.getInPointList(startPointId,startMeter,endPointId,endMeter));
    }



    /**
     * 修改危险区管理
     */
    @ApiOperation("修改危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:edit')")
    @Log(title = "危险区管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(value = {GroupUpdate.class}) BizDangerAreaDto dto)
    {
        return R.ok(bizDangerAreaService.updateEntity(dto));
    }

    /**
     * 删除危险区管理
     */
    @ApiOperation("删除危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:remove')")
    @Log(title = "危险区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dangerAreaIds}")
    public R remove(@PathVariable Long[] dangerAreaIds)
    {

        UpdateWrapper<BizDangerArea> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(BizDangerArea::getDangerAreaId, dangerAreaIds).set(BizDangerArea::getDelFlag,BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDangerAreaService.update(updateWrapper));
    }


    /**
     * 删除危险区管理
     */
    @ApiOperation("删除危险区管理")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:remove')")
    @Log(title = "危险区管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/one/{dangerAreaId}")
    public R remove(@PathVariable("dangerAreaId") Long dangerAreaId)
    {

        BizDangerArea entity = new BizDangerArea();
        entity.setDangerAreaId(dangerAreaId).setDelFlag(BizBaseConstant.DELFLAG_Y);
        return R.ok(bizDangerAreaService.updateById(entity));
    }


    public static void main(String[] args) {
        double radians = Math.toRadians(90);
        Double a = Math.sin(radians);
        System.out.println("args = " + a);
    }


}

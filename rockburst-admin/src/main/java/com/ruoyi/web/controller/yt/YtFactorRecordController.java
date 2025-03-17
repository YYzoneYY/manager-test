package com.ruoyi.web.controller.yt;

import cn.hutool.json.JSONUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.YtFactor;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.IYtFactorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private IBizTravePointService bizTravePointService;


    /**
     * 查询巷道帮管理列表
     */
    @ApiOperation("查询影响因素列表")
    @PreAuthorize("@ss.hasPermi('yt:factor:list')")
    @GetMapping("/list")
    public R<MPage<YtFactor>> list(@ParameterObject YtFactor dto, @ParameterObject Pagination pagination)
    {
        MPage<YtFactor> list = ytFactorService.selectEntityList(dto,pagination);
        return R.ok(list);
    }


//
//    @ApiOperation("返回影响因素坐标")
//    @PreAuthorize("@ss.hasPermi('yt:factor:list')")
//    @GetMapping("/ssss")
//    public R<MPage<YtFactor>> ssss(@ParameterObject YtFactor dto)
//    {
//        MPage<YtFactor> list = ytFactorService.selectEntityList(dto);
//        return R.ok(list);
//    }





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
        BizPresetPoint presetstart =  bizTravePointService.getPointLatLon(dto.getStartPointId(),dto.getStartMeter());
        BizPresetPoint presetend = bizTravePointService.getPointLatLon(dto.getEndPointId(),dto.getEndMeter());
        Map<String,Object> map = new HashMap<>();
        map.put("lat",presetstart.getLatitude());
        map.put("lng",presetstart.getLongitude());
        map.put("count",dto.getValue());

        Map<String,Object> map1 = new HashMap<>();
        map1.put("lat",presetend.getLatitude());
        map1.put("lng",presetend.getLongitude());
        map1.put("count",dto.getValue());
        List<Map<String,Object>> list = new ArrayList<>();
        list.add(map);
        list.add(map1);
        dto.setLatlngs(JSONUtil.parseArray(list).toString());
//        {lat: 35.3655868687,lng:108.8149548921,count:50},
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
        BizPresetPoint presetstart =  bizTravePointService.getPointLatLon(dto.getStartPointId(),dto.getStartMeter());
        BizPresetPoint presetend = bizTravePointService.getPointLatLon(dto.getEndPointId(),dto.getEndMeter());
        Map<String,Object> map = new HashMap<>();
        map.put("lat",presetstart.getLatitude());
        map.put("lng",presetstart.getLongitude());
        map.put("count",dto.getValue());

        Map<String,Object> map1 = new HashMap<>();
        map1.put("lat",presetend.getLatitude());
        map1.put("lng",presetend.getLongitude());
        map1.put("count",dto.getValue());
        List<Map<String,Object>> list = new ArrayList<>();
        list.add(map);
        list.add(map1);
        dto.setLatlngs(JSONUtil.parseArray(list).toString());
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

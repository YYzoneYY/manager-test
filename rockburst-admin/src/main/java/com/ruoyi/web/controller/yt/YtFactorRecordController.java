package com.ruoyi.web.controller.yt;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.constant.BizBaseConstant;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.YtFactor;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.IYtFactorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @Autowired
    private ISysConfigService configService;



    /**
     * 查询巷道帮管理列表
     */
//    @ApiOperation("查询影响因素列表")
//    @PreAuthorize("@ss.hasPermi('yt:factor:list')")
//    @GetMapping("/list")
//    public R<MPage<YtFactor>> list(@ParameterObject YtFactor dto, @ParameterObject Pagination pagination)
//    {
//        MPage<YtFactor> list = ytFactorService.selectEntityList(dto,pagination);
//        return R.ok(list);
//    }


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
        dto.setMaster(1);
        ytFactorService.save(dto);
        JSONArray latlngs = null;
        if(StrUtil.isNotEmpty(dto.getLatlngs())){
            latlngs = JSONUtil.parseArray(dto.getLatlngs());
        }
        JSONArray angles = null;
        if(StrUtil.isNotEmpty(dto.getAngles())){
            angles = JSONUtil.parseArray(dto.getAngles());
        }
        if(latlngs != null && angles != null){
            String bil = configService.selectConfigByKey("ytbil");
            List<List<Map<String,Object>>> all = new ArrayList<>();
            for (Object angle_o : angles) {
                JSONObject angle = JSONUtil.parseObj(angle_o);
                Integer meter = angle.getInt("meter");

                for (int i = 0; i <= meter; i++) {
                    System.out.println("i = " + i);
                    List<Map<String,Object>> list = new ArrayList<>();
                    if(i == meter){
                        for (Object latlng_o : latlngs) {
                            Map<String,Object> map = new HashMap<>();
                            JSONObject latlng = JSONUtil.parseObj(latlng_o);

                            BigDecimal meter_decimal = new BigDecimal(bil).multiply(new BigDecimal(i));
                            BizPresetPoint point = bizTravePointService.getLatLontop(latlng.getStr("lat"),latlng.getStr("lng"),meter_decimal.doubleValue(),angle.getInt("angle"));
//                            map.put("lat",Double.parseDouble(point.getLatitudet()));
//                            map.put("lng",Double.parseDouble(point.getLongitudet()));
                            map.put("count",1);
                            list.add(map);
                        }
                        YtFactor ytFactor = new YtFactor();
                        ytFactor.setName(dto.getName()+"_")
                                .setLatlngs(JSONUtil.toJsonStr(list))
                                .setFactorType(dto.getFactorType())
                                .setMaster(2);
                        ytFactorService.save(ytFactor);
                    }
                    if(i%10 == 0 && i != 0 && i != meter){
                        for (Object latlng_o : latlngs) {
                            Map<String,Object> map = new HashMap<>();
                            JSONObject latlng = JSONUtil.parseObj(latlng_o);
                            Integer count_ =latlng.getInt("count");
                            int rule = count_/(meter/10);
                            BigDecimal meter_decimal = new BigDecimal(bil).multiply(new BigDecimal(i));
                            BizPresetPoint point = bizTravePointService.getLatLontop(latlng.getStr("lat"),latlng.getStr("lng"),meter_decimal.doubleValue(),angle.getInt("angle"));
//                            map.put("lat",Double.parseDouble(point.getLatitudet()));
//                            map.put("lng",Double.parseDouble(point.getLongitudet()));
                            map.put("count",count_ - rule*(i/10));
                            list.add(map);
                        }
                        YtFactor ytFactor = new YtFactor();
                        ytFactor.setName(dto.getName()+"_")
                                .setLatlngs(JSONUtil.toJsonStr(list))
                                .setFactorType(dto.getFactorType())
                                .setMaster(2);
                        ytFactorService.save(ytFactor);
                    }
                }
            }
        }
        return R.ok();
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

        ytFactorService.updateById(dto);
        UpdateWrapper<YtFactor> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(YtFactor::getMasterId,dto.getFactorId()).set(YtFactor::getDelFlag, BizBaseConstant.DELFLAG_Y);
        ytFactorService.update(updateWrapper);
        JSONArray latlngs = null;
        if(StrUtil.isNotEmpty(dto.getLatlngs())){
            latlngs = JSONUtil.parseArray(dto.getLatlngs());
        }
        JSONArray angles = null;
        if(StrUtil.isNotEmpty(dto.getAngles())){
            angles = JSONUtil.parseArray(dto.getAngles());
        }
        if(latlngs != null && angles != null){
            String bil = configService.selectConfigByKey("ytbil");
            List<List<Map<String,Object>>> all = new ArrayList<>();
            for (Object angle_o : angles) {
                JSONObject angle = JSONUtil.parseObj(angle_o);
                Integer meter = angle.getInt("meter");
                for (Integer i = 0; i <= meter; i++) {
                    List<Map<String,Object>> list = new ArrayList<>();
                    if(i == meter){
                        for (Object latlng_o : latlngs) {
                            Map<String,Object> map = new HashMap<>();
                            JSONObject latlng = JSONUtil.parseObj(latlng_o);
                            BigDecimal meter_decimal = new BigDecimal(bil).multiply(new BigDecimal(i));
                            BizPresetPoint point = bizTravePointService.getLatLontop(latlng.getStr("lat"),latlng.getStr("lng"),meter_decimal.doubleValue(),angle.getInt("angle"));
//                            map.put("lat",Double.parseDouble(point.getLatitudet()));
//                            map.put("lng",Double.parseDouble(point.getLongitudet()));
                            map.put("count",1);
                            list.add(map);
                        }
                        YtFactor ytFactor = new YtFactor();
                        ytFactor.setName(dto.getName()+"_")
                                .setLatlngs(JSONUtil.toJsonStr(list))
                                .setFactorType(dto.getFactorType())
                                .setMaster(2);
                        ytFactorService.save(ytFactor);
                        continue;
                    }
                    if(i%10 == 0 && i != 0 && i != meter){
                        for (Object latlng_o : latlngs) {
                            Map<String,Object> map = new HashMap<>();
                            JSONObject latlng = JSONUtil.parseObj(latlng_o);
                            Integer count_ =latlng.getInt("count");
                            int rule = count_/(meter/10);
                            BigDecimal meter_decimal = new BigDecimal(bil).multiply(new BigDecimal(i));
                            BizPresetPoint point = bizTravePointService.getLatLontop(latlng.getStr("lat"),latlng.getStr("lng"),meter_decimal.doubleValue(),angle.getInt("angle"));
//                            map.put("lat",Double.parseDouble(point.getLatitudet()));
//                            map.put("lng",Double.parseDouble(point.getLongitudet()));
                            map.put("count",count_ - rule*(i/10));
                            list.add(map);
                        }
                        YtFactor ytFactor = new YtFactor();
                        ytFactor.setName(dto.getName()+"_")
                                .setLatlngs(JSONUtil.toJsonStr(list))
                                .setFactorType(dto.getFactorType())
                                .setMaster(2);
                        ytFactorService.save(ytFactor);
                    }
                }
            }
        }
        return R.ok();
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


        UpdateWrapper<YtFactor> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(YtFactor::getFactorId, factorId).set(YtFactor::getDelFlag, 2);
        ytFactorService.update(wrapper);
        wrapper.clear();
        wrapper.lambda().eq(YtFactor::getMasterId, factorId).set(YtFactor::getDelFlag, 2);
        ytFactorService.update(wrapper);
        return R.ok();
    }


}

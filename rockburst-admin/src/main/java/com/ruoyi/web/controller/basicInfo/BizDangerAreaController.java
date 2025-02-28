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
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.service.IBizDangerAreaService;
import com.ruoyi.system.service.IBizPresetPointService;
import com.ruoyi.system.service.IBizTravePointService;
import com.ruoyi.system.service.TunnelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    @Autowired
    private IBizPresetPointService bizPresetPointService;


    /**
     *
     */
    @ApiOperation("查询危险区管理列表")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:list')")
    @GetMapping("/list")
    public R<MPage<BizDangerAreaVo>> list(@ParameterObject BizDangerAreaDto dto, @ParameterObject Pagination pagination)
    {
        return R.ok(bizDangerAreaService.selectEntityList(dto, pagination));
    }


    /**
     * 获取危险区管理详细信息
     */
    @ApiOperation("获取危险区管理详细信息")
    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:query')")
    @GetMapping(value = "/{dangerAreaId}")
    public R<BizDangerAreaVo> getInfo(@PathVariable("dangerAreaId") Long dangerAreaId)
    {
        return R.ok(bizDangerAreaService.selectEntityById(dangerAreaId));
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
//    @PreAuthorize("@ss.hasPermi('basicInfo:dangerArea:sss')")
    @Log(title = "危险区管理", businessType = BusinessType.INSERT)
    @PostMapping("/addpre")
    public R addpre( Long workfaceId,String drillType)
    {

        QueryWrapper<TunnelEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TunnelEntity::getWorkFaceId, workfaceId);
        List<TunnelEntity> tunnelEntities  =  tunnelService.list(queryWrapper);
        for (TunnelEntity tunnelEntity : tunnelEntities) {
            List<BizDangerArea> areas = getAreaSort(tunnelEntity.getTunnelId());
            if(areas != null && areas.size() > 0){
                for (BizDangerArea area : areas) {
                    System.out.println("area.getDangerAreaId() = " + area.getDangerAreaId());
                    initAreaPrePoint1(area.getDangerAreaId(),area.getTunnelId(),drillType);
                }
            }
        }

        return R.ok();
    }



    public List<BizDangerArea> getAreaSort(Long tunnelId){
        QueryWrapper<BizDangerArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizDangerArea::getTunnelId,tunnelId);
        List<BizDangerArea> list = bizDangerAreaService.list(queryWrapper);
        list.stream().sorted(Comparator.comparing(BizDangerArea::getNo)).collect(Collectors.toList());
        return list;
    }

    public void initAreaPrePoint1(Long areaId,Long tunnelId,String drillType){
        BizDangerArea dangerArea =  bizDangerAreaService.getByIdDeep(areaId);
        BizPresetPoint point = ooo(areaId,dangerArea.getStartPointId(),dangerArea.getStartMeter(),dangerArea.getDangerLevel().getSpaced(),drillType);
    }

    public void initAreaPrePoint(Long areaId,Long tunnelId){
        //获取当前区域
        //获取当前区域的起始导线点 加 距离  ( 规定规则, 一定为 导线点 前 n 米)
        BizDangerArea dangerArea =  bizDangerAreaService.getByIdDeep(areaId);
        BizPresetPoint point = bizTravePointService.getPresetPoint(dangerArea.getStartPointId(),dangerArea.getStartMeter(),dangerArea.getDangerLevel().getSpaced());
        point.setDangerAreaId(areaId).setTunnelId(tunnelId);

        BizTravePoint currentPoint = bizTravePointService.getById(point.getPointId());

        BizTravePoint prePoint = bizTravePointService.getPrePoint(point.getPointId());
        BizTravePoint afterPoint = bizTravePointService.getNextPoint(point.getPointId());

        //存在前一个导线点的情况
        if(prePoint != null && prePoint.getPointId() != null){
            //计算 坐标
            BigDecimal latSum =  axisSum(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()));
            BigDecimal lonSum =  axisSum(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()));

            BigDecimal latMove = latSum.divide(new BigDecimal(currentPoint.getPrePointDistance())).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());
            BigDecimal lonMove = lonSum.divide(new BigDecimal(currentPoint.getPrePointDistance())).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());


            BigDecimal lat = getAxis(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()),latMove);
            BigDecimal lon = getAxis(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()),lonMove);

            point.setLatitude(lat+"").setLongitude(lon+"");
        }else if(afterPoint != null && afterPoint.getPointId() != null){

            BigDecimal latSum =  axisSum(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(afterPoint.getLatitude()));
            BigDecimal lonSum =  axisSum(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(afterPoint.getLongitude()));

            BigDecimal latMove = latSum.divide(new BigDecimal(afterPoint.getPrePointDistance())).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());
            BigDecimal lonMove = lonSum.divide(new BigDecimal(afterPoint.getPrePointDistance())).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());

            BigDecimal lat = getAxis1(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()),latMove);
            BigDecimal lon = getAxis1(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()),lonMove);

            point.setLatitude(lat+"").setLongitude(lon+"");
        }

    }

    public void init(Long areaId,Long tunnelId){

        BizDangerArea dangerArea =  bizDangerAreaService.getByIdDeep(areaId);

        BizPresetPoint point = bizTravePointService.getPresetPoint(dangerArea.getStartPointId(),dangerArea.getStartMeter(),dangerArea.getDangerLevel().getSpaced());

    }

    public BizPresetPoint ooo(Long  areaId , Long id, Double meter, Double spaced ,String drillType){
        BizPresetPoint point = bizTravePointService.getPresetPoint(id,meter,spaced);
        if(  point == null){
            return null;
        }
        Long inAreaId = bizTravePointService.judgePointInArea(point.getPointId(),point.getMeter());
        //超出危险区 或者 后面没有导线点了
        if( inAreaId == null || inAreaId != areaId){
            return null;
        }


        point = setAxis(areaId,point.getPointId(),point.getMeter());
        point.setDrillType(drillType);
//        sssss(x,jio,point);
        bizPresetPointService.savebarPresetPoint(point);


        return ooo(areaId,point.getPointId(),point.getMeter(),spaced,drillType);
    }



    public BizPresetPoint setAxis(Long  areaId  , Long currentPointId, Double meter) {

        BizPresetPoint point = new BizPresetPoint();

        point.setPointId(currentPointId).setMeter(new BigDecimal(meter).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()).setDangerAreaId(areaId);

        BizTravePoint currentPoint = bizTravePointService.getById(currentPointId);

        BizTravePoint prePoint = bizTravePointService.getPrePoint(currentPointId);
        BizTravePoint afterPoint = bizTravePointService.getNextPoint(currentPointId);

        //存在前一个导线点的情况
        if(prePoint != null && prePoint.getPointId() != null){
            //计算 坐标
            BigDecimal latSum =  axisSum(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()));
            BigDecimal lonSum =  axisSum(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()));

            BigDecimal latMove = latSum.divide(new BigDecimal(currentPoint.getPrePointDistance()),BigDecimal.ROUND_HALF_UP).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());
            BigDecimal lonMove = lonSum.divide(new BigDecimal(currentPoint.getPrePointDistance()),BigDecimal.ROUND_HALF_UP).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());


            BigDecimal lat = getAxis(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(prePoint.getLatitude()),latMove);
            BigDecimal lon = getAxis(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(prePoint.getLongitude()),lonMove);

            point.setLatitude(lat+"").setLongitude(lon+"");
        }else if(afterPoint != null && afterPoint.getPointId() != null){

            BigDecimal latSum =  axisSum(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(afterPoint.getLatitude()));
            BigDecimal lonSum =  axisSum(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(afterPoint.getLongitude()));

            BigDecimal latMove = latSum.divide(new BigDecimal(afterPoint.getPrePointDistance())).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());
            BigDecimal lonMove = lonSum.divide(new BigDecimal(afterPoint.getPrePointDistance())).setScale(8, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(point.getMeter()).abs());

            BigDecimal lat = getAxis1(new BigDecimal(currentPoint.getLatitude()),new BigDecimal(afterPoint.getLatitude()),latMove).setScale(8, BigDecimal.ROUND_HALF_UP);
            BigDecimal lon = getAxis1(new BigDecimal(currentPoint.getLongitude()),new BigDecimal(afterPoint.getLongitude()),lonMove).setScale(8, BigDecimal.ROUND_HALF_UP);

            point.setLatitude(lat+"").setLongitude(lon+"");
        }
        return point;
    }
    /**
     * 坐标求和
     * @return
     */
    public BigDecimal axisSum(BigDecimal axis1, BigDecimal axis2){
//        if(axis2.signum() == 1 && axis1.signum() == 1){
//            return axis2.add(axis1);
//        }
//        if(axis2.signum() == -1 && axis1.signum() == -1){
//            return axis2.add(axis1).abs();
//        }
//        if(axis2.signum() == -1 && axis1.signum() == 1){
//            return axis2.abs().add(axis1);
//        }
//        if(axis2.signum() == 1 && axis1.signum() == -1){
//            return axis2.add(axis1.abs());
//        }
        return axis2.subtract(axis1).abs();
    }

    public BigDecimal getAxis(BigDecimal axisCurrent, BigDecimal axisPre, BigDecimal move){
        if(axisCurrent.compareTo(axisPre) == 1){
            return axisCurrent.subtract(move).setScale(8, BigDecimal.ROUND_HALF_UP);
        }
        if(axisCurrent.compareTo(axisPre) == -1){
            return axisCurrent.add(move).setScale(8, BigDecimal.ROUND_HALF_UP);
        }

        return axisCurrent.setScale(8, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getAxis1(BigDecimal axisCurrent, BigDecimal axisAfter, BigDecimal move){
        if(axisAfter.compareTo(axisCurrent) == 1){
            return axisCurrent.subtract(move);
        }
        if(axisAfter.compareTo(axisCurrent) == -1){
            return axisCurrent.add(move);
        }

        return axisCurrent;
    }

    @Anonymous
    @ApiOperation("获取区域内,所有导线点")
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



    public BizPresetPoint sssss(Double x,Integer jio,BizPresetPoint point){
        BigDecimal lonMove =  new BigDecimal(Math.sin(Math.toRadians(jio))).multiply(new BigDecimal(x));
        BigDecimal latMove =  new BigDecimal(Math.cos(Math.toRadians(jio))).multiply(new BigDecimal(x));
        point.setLatitudet(new BigDecimal(point.getLatitudet()).add(latMove)+"");
        point.setLongitudet(new BigDecimal(point.getLongitudet()).add(lonMove)+"");
        return point;
    }
    public static void main(String[] args) {
        double radians = Math.toRadians(90);
        Double a = Math.sin(radians);
        System.out.println("args = " + a);
    }


}

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
import com.ruoyi.system.constant.MapConfigConstant;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.utils.GeometryUtil;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
    private IBizDangerLevelService bizDangerLevelService;

    @Autowired
    private TunnelService tunnelService;

    @Autowired
    private IBizTravePointService bizTravePointService;
    @Autowired
    private IBizPresetPointService bizPresetPointService;
    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;
    @Autowired
    private ISysConfigService configService;

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
            QueryWrapper<BizDangerArea> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(BizDangerArea::getTunnelId,tunnelEntity.getTunnelId()).orderByAsc(BizDangerArea::getNo);
            List<BizDangerArea> areas = bizDangerAreaService.list(queryWrapper1);
            List<BizDangerLevel> levels = bizDangerLevelService.list();
            //非生产帮
            List<Segment> fsegments = new ArrayList<>();
            //生产帮
            List<Segment> ssegments = new ArrayList<>();
            for (BizDangerArea area : areas) {
                Double spaced = new Double("1");
                for (BizDangerLevel level : levels) {
                    if(area.getLevel().equals(level.getLevel())){
                        spaced = level.getSpaced();
                    }
                }
                Segment segmentf = GeometryUtil.getSegment(area.getFscbStartx(),area.getFscbStarty(),area.getFscbEndx(),area.getFscbEndy(),spaced.toString(),area.getDangerAreaId());
                fsegments.add(segmentf);

                Segment segments = GeometryUtil.getSegment(area.getScbStartx(),area.getScbStarty(),area.getScbEndx(),area.getScbEndy(),spaced.toString(),area.getDangerAreaId());
                ssegments.add(segments);
            }
            List<Point2D> fpoint2DS =  samplePoints(fsegments);
            QueryWrapper<BizTunnelBar> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda().eq(BizTunnelBar::getTunnelId,tunnelEntity.getTunnelId()).eq(BizTunnelBar::getType,"scb");
            List<BizTunnelBar> bars =  bizTunnelBarMapper.selectList(queryWrapper2);
            BizTunnelBar scbbar = bars.get(0);
            queryWrapper2.clear();
            queryWrapper2.lambda().eq(BizTunnelBar::getTunnelId,tunnelEntity.getTunnelId()).eq(BizTunnelBar::getType,"fscb");
            List<BizTunnelBar> bars1 =  bizTunnelBarMapper.selectList(queryWrapper2);
            BizTunnelBar fsbbar = bars1.get(0);


            String uploadUrl = configService.selectConfigByKey(MapConfigConstant.map_bili);

            for (Point2D point2D : fpoint2DS) {
                BigDecimal[] biaisai  =  GeometryUtil.getExtendedPoint(point2D.getX().toString(),point2D.getY().toString(),fsbbar.getDirectAngle(),3,Double.parseDouble(uploadUrl));

                List<Map<String,Object>> list = new ArrayList<>();
                Map<String,Object> map = new HashMap<>();
                map.put("x",point2D.getX());
                map.put("y",point2D.getY());
                Map<String,Object> map1 = new HashMap<>();
                map1.put("x",biaisai[0]);
                map1.put("y",biaisai[1]);
                list.add(map);
                list.add(map1);
                BizPresetPoint bp = new BizPresetPoint();
                bp.setTunnelId(tunnelEntity.getTunnelId())
                        .setDangerAreaId(point2D.getAreaId())
                        .setWorkfaceId(workfaceId)
                        .setTunnelBarId(fsbbar.getBarId())
                        .setDrillType(drillType)
                        .setAxiss(list.toString());
                bizPresetPointService.save(bp);
            }

            List<Point2D> spoint2DS =  samplePoints(ssegments);
            for (Point2D point2D : spoint2DS) {
                BigDecimal[] biaisai  =  GeometryUtil.getExtendedPoint(point2D.getX().toString(),point2D.getY().toString(),scbbar.getDirectAngle(),3,Double.parseDouble(uploadUrl));

                List<Map<String,Object>> list = new ArrayList<>();
                Map<String,Object> map = new HashMap<>();
                map.put("x",point2D.getX());
                map.put("y",point2D.getY());
                Map<String,Object> map1 = new HashMap<>();
                map1.put("x",biaisai[0]);
                map1.put("y",biaisai[1]);
                list.add(map);
                list.add(map1);
                BizPresetPoint bp = new BizPresetPoint();
                bp.setTunnelId(tunnelEntity.getTunnelId())
                        .setDangerAreaId(point2D.getAreaId())
                        .setWorkfaceId(workfaceId)
                        .setTunnelBarId(scbbar.getBarId())
                        .setDrillType(drillType)
                        .setAxiss(list.toString());

                bizPresetPointService.save(bp);
            }
            
        }


        return R.ok();
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


    public List<Point2D> getPoint2Ds(List<Segment> segments){
        List<Point2D> sampledPoints = samplePoints(segments);
        // 输出所有点
        return sampledPoints;
    }



//    static List<Point2D> samplePoints(List<Segment> segments) {
//        List<Point2D> points = new ArrayList<>();
//        for (int i = 0; i < segments.size(); i++) {
//            Segment current = segments.get(i);
//            Segment next = (i + 1 < segments.size()) ? segments.get(i + 1) : null;
//
//            BigDecimal dx = current.getEnd().getX().subtract(current.getStart().getX());
//            BigDecimal dy = current.getEnd().getY().subtract(current.getStart().getY());
//            BigDecimal length = hypot(dx, dy, 20); // 保留20位精度
//
//            BigDecimal interval = current.getInterval();
//            if (next != null) {
//                interval = Math.min(current.getInterval(), next.getInterval());
//            }
//
//            int steps = (int) Math.floor(length / interval);
//            for (int j = 0; j < steps; j++) {
//                double ratio = (j * interval) / length;
//                double x = current.getStart().getX() + dx * ratio;
//                double y = current.getStart().getY() + dy * ratio;
//                points.add(new Point2D.Double(x, y));
//            }
//        }
//
//        // 加上最后一个点
//        points.add(segments.get(segments.size() - 1).getEnd());
//        return points;
//    }



    static List<Point2D> samplePoints(List<Segment> segments) {
        List<Point2D> points = new ArrayList<>();
        int precision = 20;

        for (int i = 0; i < segments.size(); i++) {
            Segment current = segments.get(i);
            Segment next = (i + 1 < segments.size()) ? segments.get(i + 1) : null;

            BigDecimal dx = current.getEnd().getX().subtract(current.getStart().getX());
            BigDecimal dy = current.getEnd().getY().subtract(current.getStart().getY());
            BigDecimal length = GeometryUtil.hypot(dx, dy, precision);

            BigDecimal interval = current.getInterval();
            if (next != null) {
                interval = interval.min(next.getInterval());
            }

            if (interval.compareTo(BigDecimal.ZERO) <= 0) continue;

            int steps = length.divide(interval, RoundingMode.FLOOR).intValue();

            for (int j = 0; j < steps; j++) {
                BigDecimal ratio = interval.multiply(BigDecimal.valueOf(j))
                        .divide(length, precision, RoundingMode.HALF_UP);
                BigDecimal x = current.getStart().getX().add(dx.multiply(ratio));
                BigDecimal y = current.getStart().getY().add(dy.multiply(ratio));
                points.add(new Point2D(x, y,current.getAreaId()));
            }
        }

        // 添加最后一个点
        if (!segments.isEmpty()) {
            Point2D end = segments.get(segments.size() - 1).getEnd();
            end.setAreaId(segments.get(segments.size() - 1).getAreaId());
            points.add(end);
        }

        return points;
    }



}

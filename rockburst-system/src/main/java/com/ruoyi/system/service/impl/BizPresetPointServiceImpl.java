package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizPlanPrePointDto;
import com.ruoyi.system.domain.dto.BizPresetPointDto;
import com.ruoyi.system.domain.dto.Coordinate.ConvertPoint;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.BizPlanPresetService;
import com.ruoyi.system.service.IBizPresetPointService;
import com.ruoyi.system.service.IBizTravePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Slf4j
@Service
public class BizPresetPointServiceImpl extends ServiceImpl<BizPresetPointMapper, BizPresetPoint> implements IBizPresetPointService
{


    @Autowired
    private BizPresetPointMapper bizPresetPointMapper;

    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;
    @Autowired
    private IBizTravePointService bizTravePointService;

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private BizPlanPresetMapper bizPlanPresetMapper;
    @Autowired
    private BizTravePointMapper bizTravePointMapper;
    @Autowired
    private TunnelMapper tunnelMapper;

    @Resource
    private BizDangerAreaMapper bizDangerAreaMapper;

    @Resource
    private BizPlanPresetService bizPlanPresetService;


    @Override
    public BizPresetPoint selectEntityById(Long id) {
        return bizPresetPointMapper.selectById(id);
    }

    @Override
    public MPage<BizPresetPoint> selectEntityList(BizPresetPointDto dto, Pagination pagination) {
        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        IPage<BizPresetPoint> list = bizPresetPointMapper.selectPage(pagination,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(BizPresetPointDto dto) {
        BizPresetPoint bizPresetPoint = new BizPresetPoint();
        BeanUtil.copyProperties(dto, bizPresetPoint);
        return bizPresetPointMapper.insert(bizPresetPoint);
    }

    @Override
    public int updateEntity(BizPresetPointDto dto) {
        BizPresetPoint bizPresetPoint = new BizPresetPoint();
        BeanUtil.copyProperties(dto, bizPresetPoint);
        return bizPresetPointMapper.updateById(bizPresetPoint);
    }

    @Override
    public boolean setPlanPrePoint(Long planId, List<BizPlanPrePointDto> dtos) {
        log.info("计划id:{},计划参数:{}",planId, JSONUtil.parse(dtos));

        try {
            PlanEntity entity =  planMapper.selectById(planId);

            List<BizPresetPoint> bizPresetPoints = new ArrayList<>();
            for (BizPlanPrePointDto dto : dtos) {
                //
                if(dto.getStartPointId() == null || dto.getEndPointId() == null){
                    continue;
                }
                List<BizTravePoint> points =  bizTravePointService.getPointByRange(dto.getStartPointId(), dto.getEndPointId());

                TunnelEntity tunnel = tunnelMapper.selectById(dto.getTunnelId());

                BizPresetPoint start = bizTravePointService.getPointPre(dto.getStartPointId(),dto.getStartMeter());
                BizPresetPoint end = bizTravePointService.getPointPre(dto.getEndPointId(),dto.getEndMeter());

                if(points != null && points.size() > 0){
                    List<Long> pointIds =  points.stream().map(BizTravePoint::getPointId).collect(Collectors.toList());
                    QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
                    if(start != null ){
                        pointIds.remove(start.getPointId());

                    }
                    if( end != null){
                        pointIds.remove(end.getPointId());
                    }

                    queryWrapper.lambda().in(BizPresetPoint::getPointId,pointIds).isNull(BizPresetPoint::getProjectId);
                    List<BizPresetPoint> xxs = this.getBaseMapper().selectList(queryWrapper);
                    bizPresetPoints.addAll(xxs);
                }

                List<BizPresetPoint> startpoints = this.getPrePointByPointMeterstart(dto.getStartPointId(),dto.getStartMeter(),entity.getDrillType());
                List<BizPresetPoint> endpoints = this.getPrePointByPointMeterend(dto.getEndPointId(),dto.getEndMeter(),entity.getDrillType());
                if(startpoints != null && !startpoints.isEmpty()){
                    bizPresetPoints.addAll(startpoints);
                }
                if(endpoints != null && !endpoints.isEmpty()){
                    bizPresetPoints.addAll(endpoints);
                }
            }
            for (BizPresetPoint dto : bizPresetPoints) {
                BizPlanPreset bizPlanPreset = new BizPlanPreset();
                bizPlanPreset.setPlanId(planId)
                        .setDangerAreaId(dto.getDangerAreaId())
                        .setPresetPointId(dto.getPresetPointId())
                        .setBottom(dto.getAxisy()+","+dto.getAxisx());
//                    .setTop(dto.getLongitudet()+","+dto.getLatitudet());
                bizPlanPresetMapper.insert(bizPlanPreset);
            }
            return true;
        }catch (Exception e){
            return true;
        }finally {
            return true;
        }

    }

    @Override
    public boolean setPlanPrePointNew(Long planId, List<BizPlanPrePointDto> dtos) {
        try {
            ArrayList<BizPlanPreset> bizPlanPresets = new ArrayList<>();
            PlanEntity entity =  planMapper.selectById(planId);
            for (BizPlanPrePointDto dto : dtos) {

                if(dto.getStartPointCoordinate() == null || dto.getEndPointCoordinate() == null){
                    continue;
                }
                String[] partsStart = StringUtils.split(dto.getStartPointCoordinate(), ',');
                String[] partsEnd = StringUtils.split(dto.getEndPointCoordinate(), ',');

                if (partsStart.length == 0 || partsEnd.length == 0) {
                    continue;
                }

                double planStartXFmt = Double.parseDouble(partsStart[0]); // 计划开始导线点 x坐标
                double planEndXFmt = Double.parseDouble(partsEnd[0]); // 计划结束导线点 x坐标

                List<Long> dangerAreaIds = new ArrayList<>();
                List<Long> dangerIds  = new ArrayList<>();
                List<Long> areaIds = new ArrayList<>();
                List<Long> specialDangerIds = new ArrayList<>();

                List<BizDangerArea> bizDangerAreas = bizDangerAreaMapper.selectList(new LambdaQueryWrapper<BizDangerArea>()
                        .eq(BizDangerArea::getWorkfaceId, entity.getWorkFaceId())
                        .eq(BizDangerArea::getTunnelId, dto.getTunnelId())
                        .eq(BizDangerArea::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                if (bizDangerAreas != null && !bizDangerAreas.isEmpty()) {
                    for (BizDangerArea bizDangerArea : bizDangerAreas) {
                        double scbStartXFmt = Double.parseDouble(bizDangerArea.getScbStartx()); // 生产帮开始导线点 x坐标
                        double scbEndXFmt = Double.parseDouble(bizDangerArea.getScbEndx()); // 生产帮结束导线点 x坐标
                        // 判断危险区是否在计划区域内
                        if (scbStartXFmt >= planStartXFmt && scbEndXFmt <= planEndXFmt) {
                            dangerAreaIds.add(bizDangerArea.getDangerAreaId());
                        }
                        // 查找危险区一部分在计划区域内的数据
                        if (scbStartXFmt >= planStartXFmt && planEndXFmt < scbEndXFmt) {
                            dangerIds.add(bizDangerArea.getDangerAreaId());
                        }
                        if (scbEndXFmt <= planEndXFmt && planStartXFmt > scbStartXFmt) {
                            areaIds.add(bizDangerArea.getDangerAreaId());
                        }
                        // 计划区域在危险区内但不全部包含危险区
                        if (planStartXFmt > scbStartXFmt && planEndXFmt < scbEndXFmt) {
                            specialDangerIds.add(bizDangerArea.getDangerAreaId());
                        }
                    }
                    List<Long> allDangerAreaIds = new ArrayList<>(dangerAreaIds);
                    allDangerAreaIds.addAll(dangerIds);
                    if (!allDangerAreaIds.isEmpty()) {
                        List<BizPresetPoint> presetPoints = bizPresetPointMapper.selectList(new LambdaQueryWrapper<BizPresetPoint>()
                                .in(BizPresetPoint::getDangerAreaId, allDangerAreaIds)
                                .eq(BizPresetPoint::getTunnelId, dto.getTunnelId())
                                .eq(BizPresetPoint::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

                        for (BizPresetPoint point : presetPoints) {
                            double axisX = 0.0;
                            Long dangerAreaId = getMatchingDangerAreaId(point, dangerAreaIds, dangerIds, areaIds, specialDangerIds);
                            if (dangerAreaId == null) continue;

                            String axiss = point.getAxiss();
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<ConvertPoint> convertPoints = objectMapper.readValue(axiss, new TypeReference<List<ConvertPoint>>() {});
                            if (!convertPoints.isEmpty()) {
                                // 取坐标点位集合中第一个元素
                                ConvertPoint firstPoint = convertPoints.get(0);
                                axisX = firstPoint.x;
                            }

//                            double axisXFmt = Double.parseDouble(point.getAxisx());
                            if (dangerIds.contains(dangerAreaId)) {
                                if (axisX > planEndXFmt) {
                                    continue;
                                }
                            }
                            if (areaIds.contains(dangerAreaId)) {
                                if (axisX < planStartXFmt) {
                                    continue;
                                }
                            }
                            if (specialDangerIds.contains(dangerAreaId)) {
                                if (axisX > planStartXFmt && axisX < planEndXFmt) {
                                    continue;
                                }
                            }

                            BizPlanPreset preset = new BizPlanPreset();
                            preset.setPlanId(planId)
                                    .setDangerAreaId(dangerAreaId)
                                    .setPresetPointId(point.getPresetPointId())
                                    .setBottom(point.getAxisy() + "," + point.getAxisx());
                            bizPlanPresets.add(preset);
                        }
                    }
                }
            }
            this.bizPlanPresetService.saveBatch(bizPlanPresets);
            return true;
        } catch (Exception e) {
            return true;
        } finally {
            return true;
        }
    }

    private Long getMatchingDangerAreaId(BizPresetPoint point, List<Long> dangerAreaIds, List<Long> dangerIds, List<Long> areaIds, List<Long> specialDangerIds) {
        if (dangerAreaIds.contains(point.getDangerAreaId())) {
            return point.getDangerAreaId();
        } else if (dangerIds.contains(point.getDangerAreaId())) {
            return point.getDangerAreaId();
        } else if (areaIds.contains(point.getDangerAreaId())) {
            return point.getDangerAreaId();
        } else if (specialDangerIds.contains(point.getDangerAreaId())) {
            return point.getDangerAreaId();
        }
        return null;
    }


    @Override
    public List<BizPresetPoint> getPrePointByPointRange(Long startPointId, Long endPointId, String drillType) {
        //同一个导线点
        if(startPointId == endPointId){
            QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizPresetPoint::getPointId, startPointId)
                    .eq(BizPresetPoint::getDrillType, drillType);
            return bizPresetPointMapper.selectList(queryWrapper);
        }

        //必须起始小于结束
        BizTravePoint startPoint = bizTravePointService.getById(startPointId);
        BizTravePoint endPoint = bizTravePointService.getById(endPointId);
        if(startPoint.getNo() > endPoint.getNo()){
            return null;
        }

        //获取
        List<Long> pointIds = new ArrayList<>();
        List<BizTravePoint> points = bizTravePointService.getPointByRange(startPointId, endPointId);
        if(points != null && points.size() > 0){
            pointIds = points.stream().map(BizTravePoint::getPointId).collect(Collectors.toList());
        }else {
            return null;
        }
        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(BizPresetPoint::getPointId, pointIds)
                .eq(BizPresetPoint::getDrillType, drillType);
        return bizPresetPointMapper.selectList(queryWrapper);
    }

    @Override
    public List<BizPresetPoint> getPrePointByPointMeterstart(Long pointId, Double meter, String drillType) {
        if(meter == 0.0){
            QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizPresetPoint::getPointId,pointId)
                    .eq(BizPresetPoint::getMeter,0)
                    .isNull(BizPresetPoint::getProjectId)
                    .eq(BizPresetPoint::getDrillType,drillType);
            List<BizPresetPoint> list = bizPresetPointMapper.selectList(queryWrapper);
            return list;
        }
        //判断前后
        if(meter > 0){
            BizTravePoint aftePoint = bizTravePointService.getNextPoint(pointId);
            //后一个导线点不存在
            if(aftePoint == null || aftePoint.getPointId() == null){
                return null;
            }
            Double distance = aftePoint.getPrePointDistance();
            //两导线点间距离 不足 传入的距离
            if(distance - meter < 0){
                return null;
            }
            meter = meter - distance;
            pointId = aftePoint.getPointId();
        }

        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizPresetPoint::getPointId,pointId)
                .ge(BizPresetPoint::getMeter,meter)
                .isNull(BizPresetPoint::getProjectId)
                .eq(BizPresetPoint::getDrillType,drillType);
        List<BizPresetPoint> list = bizPresetPointMapper.selectList(queryWrapper);
        return list;

    }

    @Override
    public List<BizPresetPoint> getPrePointByPointMeterend(Long pointId, Double meter, String drillType) {
        if(meter == 0.0){
            QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizPresetPoint::getPointId,pointId)
                    .eq(BizPresetPoint::getMeter,0)
                    .isNull(BizPresetPoint::getProjectId)
                    .eq(BizPresetPoint::getDrillType,drillType);
            List<BizPresetPoint> list = bizPresetPointMapper.selectList(queryWrapper);
            return list;
        }
        //判断前后
        if(meter > 0){
           BizTravePoint aftePoint = bizTravePointService.getNextPoint(pointId);
           //后一个导线点不存在
           if(aftePoint == null || aftePoint.getPointId() == null){
                return null;
           }
            Double distance = aftePoint.getPrePointDistance();
           //两导线点间距离 不足 传入的距离
           if(distance - meter < 0){
               return null;
           }
           meter = meter - distance;
           pointId = aftePoint.getPointId();
        }

        QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizPresetPoint::getPointId,pointId)
                .le(BizPresetPoint::getMeter,meter)
                .isNull(BizPresetPoint::getProjectId)
                .eq(BizPresetPoint::getDrillType,drillType);
        List<BizPresetPoint> list = bizPresetPointMapper.selectList(queryWrapper);
        return list;

    }



    @Override
    public int savebarPresetPoint(BizPresetPoint dto) {
//        BizTravePoint point11 =  bizTravePointMapper.selectById(dto.getPointId());
//        //先存生产帮,再存非生产帮
//        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
//        queryWrapper.lambda().eq(BizTunnelBar::getTunnelId, point11.getTunnelId());
//        List<BizTunnelBar> tunnelBars = bizTunnelBarMapper.selectList(queryWrapper);
//        for (BizTunnelBar tunnelBar : tunnelBars) {
//            BizPresetPoint point = sssss(tunnelBar.getDirectRangePre(),tunnelBar.getDirectAngle(),dto);
//            point.setTunnelBarId(tunnelBar.getBarId());
//            point.setDrillType(dto.getDrillType());
//            point.setWorkfaceId(tunnelBar.getWorkfaceId());
//            point.setPresetPointId(null);
//            this.save(point);
//        }
        return 0;
    }


    public BizPresetPoint sssss(Double x,Integer jio,BizPresetPoint point){
        double aa = Math.sin(Math.toRadians(jio));
        double bb = Math.cos(Math.toRadians(jio));
//        BigDecimal lonMove =  new BigDecimal(aa).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
//        BigDecimal latMove =  new BigDecimal(bb).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
//        BigDecimal lat =  new BigDecimal(point.getAxisx()).setScale(8, BigDecimal.ROUND_HALF_UP);
//        point.setLatitudet(lat.add(latMove)+"");
//        point.setLongitudet(new BigDecimal(point.getAxisy()).setScale(8, BigDecimal.ROUND_HALF_UP).add(lonMove)+"");
        return point;
    }
}

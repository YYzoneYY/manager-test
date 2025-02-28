package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizPlanPreset;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.BizPlanPrePointDto;
import com.ruoyi.system.domain.dto.BizPresetPointDto;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IBizPresetPointService;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        try {
            PlanEntity entity =  planMapper.selectById(planId);

            List<BizPresetPoint> bizPresetPoints = new ArrayList<>();
            for (BizPlanPrePointDto dto : dtos) {
                //
                if(dto.getStartPointId() == null || dto.getEndPointId() == null){
                    continue;
                }
                List<BizPresetPoint> startpoints = this.getPrePointByPointMeter(dto.getStartPointId(),dto.getStartMeter(),entity.getDrillType());
                List<BizPresetPoint> endpoints = this.getPrePointByPointMeter(dto.getEndPointId(),dto.getEndMeter(),entity.getDrillType());
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
                        .setBottom(dto.getLongitude()+","+dto.getLatitude())
                    .setTop(String.join(dto.getLongitudet(),",",dto.getLongitudet()));
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
    public List<BizPresetPoint> getPrePointByPointMeter(Long pointId, Double meter, String drillType) {
        if(meter == 0.0){
            QueryWrapper<BizPresetPoint> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BizPresetPoint::getPointId,pointId)
                    .eq(BizPresetPoint::getMeter,0)
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
                .eq(BizPresetPoint::getDrillType,drillType);
        List<BizPresetPoint> list = bizPresetPointMapper.selectList(queryWrapper);
        return list;

    }



    @Override
    public int savebarPresetPoint(BizPresetPoint dto) {
        BizTravePoint point11 =  bizTravePointMapper.selectById(dto.getPointId());
        //先存生产帮,再存非生产帮
        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTunnelBar::getTunnelId, point11.getTunnelId());
        List<BizTunnelBar> tunnelBars = bizTunnelBarMapper.selectList(queryWrapper);
        for (BizTunnelBar tunnelBar : tunnelBars) {
            BizPresetPoint point = sssss(tunnelBar.getDirectRange(),tunnelBar.getDirectAngle(),dto);
            point.setTunnelBarId(tunnelBar.getBarId());
            point.setDrillType(dto.getDrillType());
            point.setPresetPointId(null);
            this.save(point);
        }
        return 0;
    }


    public BizPresetPoint sssss(Double x,Integer jio,BizPresetPoint point){
        BigDecimal lonMove =  new BigDecimal(Math.sin(Math.toRadians(jio))).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
        BigDecimal latMove =  new BigDecimal(Math.cos(Math.toRadians(jio))).multiply(new BigDecimal(x)).setScale(8, RoundingMode.HALF_UP);
        BigDecimal lat =  new BigDecimal(point.getLatitude()).setScale(8, BigDecimal.ROUND_HALF_UP);
        point.setLatitudet(lat.add(latMove)+"");
        point.setLongitudet(new BigDecimal(point.getLongitude()).setScale(8, BigDecimal.ROUND_HALF_UP).add(lonMove)+"");
        return point;
    }
}

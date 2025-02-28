package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.mapper.BizDangerAreaMapper;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizDangerAreaService;
import com.ruoyi.system.service.IBizTravePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizDangerAreaServiceImpl extends ServiceImpl<BizDangerAreaMapper, BizDangerArea> implements IBizDangerAreaService
{


    @Autowired
    private BizDangerAreaMapper bizDangerAreaMapper;

    @Autowired
    private TunnelMapper tunnelMapper;
    @Autowired
    private IBizTravePointService bizTravePointService;
    @Autowired
    private BizTravePointServiceImpl bizTravePointServiceImpl;
    @Autowired
    private BizTravePointMapper bizTravePointMapper;


    @Override
    public BizDangerAreaVo selectEntityById(Long id) {
        MPJLambdaWrapper<BizDangerArea> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BizDangerArea.class)
                .selectAssociation("t1",BizTravePoint.class, BizDangerAreaVo::getStartPoint)
                .selectAssociation("t2",BizTravePoint.class, BizDangerAreaVo::getEndPoint)
                .selectAssociation(BizDangerLevel.class, BizDangerAreaVo::getBizDangerLevel)
                .selectAssociation(BizWorkface.class, BizDangerAreaVo::getWorkface)
                .selectAssociation(TunnelEntity.class, BizDangerAreaVo::getTunnel)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizDangerArea::getStartPointId)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizDangerArea::getEndPointId)
                .leftJoin(BizDangerLevel.class,BizDangerLevel::getLevel,BizDangerArea::getLevel)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizDangerArea::getWorkfaceId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizDangerArea::getTunnelId)
                .eq( BizDangerArea::getDangerAreaId, id);
         BizDangerAreaVo vo = bizDangerAreaMapper.selectJoinOne(BizDangerAreaVo.class,queryWrapper);
        return vo;
    }

    @Override
    public int initPresetPoint(Long workfaceId) {
        return 0;
    }

    @Override
    public MPage<BizDangerAreaVo> selectEntityList(BizDangerAreaDto dto, Pagination pagination) {
        MPJLambdaWrapper<BizDangerArea> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BizDangerArea.class)
                .selectAssociation("t1",BizTravePoint.class, BizDangerAreaVo::getStartPoint)
                .selectAssociation("t2",BizTravePoint.class, BizDangerAreaVo::getEndPoint)
                .selectAssociation(BizDangerLevel.class, BizDangerAreaVo::getBizDangerLevel)
                .selectAssociation(BizWorkface.class, BizDangerAreaVo::getWorkface)
                .selectAssociation(TunnelEntity.class, BizDangerAreaVo::getTunnel)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizDangerArea::getStartPointId)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizDangerArea::getEndPointId)
                .leftJoin(BizDangerLevel.class,BizDangerLevel::getLevel,BizDangerArea::getLevel)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizDangerArea::getWorkfaceId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizDangerArea::getTunnelId)
//                .eq(dto.getDangerAreaId() != null, BizDangerArea::getWorkfaceId, dto.getWorkfaceId())
                .eq(dto.getWorkfaceId() != null , BizDangerArea::getWorkfaceId , dto.getWorkfaceId())
                .eq(StrUtil.isNotEmpty(dto.getLevel()), BizDangerArea::getLevel, dto.getLevel());
        IPage<BizDangerAreaVo> list = bizDangerAreaMapper.selectJoinPage(pagination,BizDangerAreaVo.class,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public List<BizDangerAreaVo> selectEntityCheckList(BizDangerAreaDto dto) {
        MPJLambdaWrapper<BizDangerArea> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.selectAll(BizDangerArea.class)
                .selectAssociation(BizDangerLevel.class, BizDangerAreaVo::getBizDangerLevel)
                .leftJoin(BizDangerLevel.class,BizDangerLevel::getLevel,BizDangerArea::getLevel)
                .eq(dto.getWorkfaceId() != null , BizDangerArea::getWorkfaceId , dto.getWorkfaceId())
                .eq(StrUtil.isNotEmpty(dto.getLevel()), BizDangerArea::getLevel, dto.getLevel());
        List<BizDangerAreaVo> list = bizDangerAreaMapper.selectJoinList(BizDangerAreaVo.class,queryWrapper);
        return list;
    }

    @Override
    public List<BizDangerAreaVo> selectEntityListVo(List<Long> areaIdList) {
        MPJLambdaWrapper<BizDangerArea> queryWrapper2 = new MPJLambdaWrapper<>();
        queryWrapper2
                .selectAll(BizDangerArea.class)
                .selectAssociation(BizDangerLevel.class, BizDangerAreaVo::getBizDangerLevel)
                .leftJoin(BizDangerLevel.class,BizDangerLevel::getLevel,BizDangerArea::getLevel)
                .in(BizDangerArea::getDangerAreaId,areaIdList);
        List<BizDangerAreaVo> list = bizDangerAreaMapper.selectJoinList(BizDangerAreaVo.class,queryWrapper2);
        return list;
    }

    @Override
    public int insertEntity(BizDangerAreaDto dto) {

        QueryWrapper<BizDangerArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .select(BizDangerArea::getNo)
                .eq(BizDangerArea::getWorkfaceId,dto.getTunnelId());
        List<BizDangerArea> list =  bizDangerAreaMapper.selectList(queryWrapper);
        Optional<BizDangerArea> maxArea = list.stream().max(Comparator.comparingInt(BizDangerArea::getNo));

        //todo   自动生成no

        BizDangerArea bizDangerArea = new BizDangerArea();
        BeanUtil.copyProperties(dto, bizDangerArea);
        if (maxArea.isPresent()) {
            bizDangerArea.setNo(maxArea.get().getNo()+1) ;
        }
//        Assert.isTrue(dto.getStartMeter()<=0,"必须在导线点前开始");
        if(dto.getStartMeter() > 0 ){
            BizPresetPoint startPoint = bizTravePointService.getPointPre(dto.getStartPointId(),dto.getStartMeter());
            dto.setStartMeter(startPoint.getMeter()).setStartPointId(startPoint.getPointId());
            bizDangerArea.setStartMeter(startPoint.getMeter()).setStartPointId(startPoint.getPointId());
        }
        if(dto.getEndMeter() > 0 ){
            BizPresetPoint endPoint = bizTravePointService.getPointPre(dto.getEndPointId(),dto.getEndMeter());
            dto.setEndMeter(endPoint.getMeter()).setEndPointId(endPoint.getPointId());
            bizDangerArea.setEndMeter(endPoint.getMeter()).setEndPointId(endPoint.getPointId());
        }

        if(dto.getStartPointId() != dto.getEndPointId()){
            BizTravePoint startPoint = bizTravePointMapper.selectById(dto.getStartPointId());
            BizTravePoint endPoint = bizTravePointMapper.selectById(dto.getEndPointId());
            QueryWrapper<BizTravePoint> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda()
                    .eq(BizTravePoint::getTunnelId,startPoint.getTunnelId())
                    .between(BizTravePoint::getNo,startPoint.getNo(),endPoint.getNo());
            List<BizTravePoint> list2 = bizTravePointMapper.selectList(queryWrapper2);
            List<Long> pointIds = new ArrayList<>();
            if(list2 != null && list2.size()>0){
                pointIds = list2.stream().map(BizTravePoint::getPointId).collect(Collectors.toList());
            }

            if(dto.getStartMeter() >= 0){
                pointIds.remove(dto.getStartPointId());
            }

            if(dto.getStartMeter() <= 0){
                pointIds.remove(dto.getEndPointId());
            }

            bizDangerArea.setPointlist(String.join(",", pointIds.stream()
                    .map(Object::toString)
                    .toArray(String[]::new)));
        }
        return bizDangerAreaMapper.insert(bizDangerArea);
    }

    @Override
    public int updateEntity(BizDangerAreaDto dto) {

        BizDangerArea bizDangerArea = new BizDangerArea();
        BeanUtil.copyProperties(dto, bizDangerArea);

        if (dto.getStartMeter() > 0) {
            BizPresetPoint startPoint = bizTravePointService.getPointPre(dto.getStartPointId(), dto.getStartMeter());
            dto.setStartMeter(startPoint.getMeter()).setStartPointId(startPoint.getPointId());
            bizDangerArea.setStartMeter(startPoint.getMeter()).setStartPointId(startPoint.getPointId());
        }
        if (dto.getEndMeter() > 0) {
            BizPresetPoint endPoint = bizTravePointService.getPointPre(dto.getEndPointId(), dto.getEndMeter());
            dto.setEndMeter(endPoint.getMeter()).setEndPointId(endPoint.getPointId());
            bizDangerArea.setEndMeter(endPoint.getMeter()).setEndPointId(endPoint.getPointId());
        }

        if (dto.getStartPointId() != dto.getEndPointId()) {
            BizTravePoint startPoint = bizTravePointMapper.selectById(dto.getStartPointId());
            BizTravePoint endPoint = bizTravePointMapper.selectById(dto.getEndPointId());
            QueryWrapper<BizTravePoint> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.lambda()
                    .eq(BizTravePoint::getTunnelId, startPoint.getTunnelId())
                    .between(BizTravePoint::getNo, startPoint.getNo(), endPoint.getNo());
            List<BizTravePoint> list2 = bizTravePointMapper.selectList(queryWrapper2);
            List<Long> pointIds = new ArrayList<>();
            if (list2 != null && list2.size() > 0) {
                pointIds = list2.stream().map(BizTravePoint::getPointId).collect(Collectors.toList());
            }

            if (dto.getStartMeter() >= 0) {
                pointIds.remove(dto.getStartPointId());
            }

            if (dto.getStartMeter() <= 0) {
                pointIds.remove(dto.getEndPointId());
            }

            bizDangerArea.setPointlist(String.join(",", pointIds.stream()
                    .map(Object::toString)
                    .toArray(String[]::new)));

        }
        return bizDangerAreaMapper.updateById(bizDangerArea);
    }



}

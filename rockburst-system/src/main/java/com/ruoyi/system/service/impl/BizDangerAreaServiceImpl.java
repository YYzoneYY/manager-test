package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.BizDangerLevel;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.domain.vo.BizDangerAreaVo;
import com.ruoyi.system.mapper.BizDangerAreaMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizDangerAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

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
    private BizTravePointServiceImpl bizTravePointServiceImpl;


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
    public int insertEntity(BizDangerAreaDto dto) {
        BizDangerArea bizDangerArea = new BizDangerArea();
        BeanUtil.copyProperties(dto, bizDangerArea);
        Assert.isTrue(dto.getStartMeter()<0,"必须在导线点前开始");
        return bizDangerAreaMapper.insert(bizDangerArea);
    }

    @Override
    public int updateEntity(BizDangerAreaDto dto) {
        BizDangerArea bizDangerArea = new BizDangerArea();
        BeanUtil.copyProperties(dto, bizDangerArea);
        Assert.isTrue(dto.getStartMeter()<0,"必须在导线点前开始");
        return bizDangerAreaMapper.updateById(bizDangerArea);
    }



}

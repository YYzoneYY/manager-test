package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.BizTunnelBarDto;
import com.ruoyi.system.domain.vo.BizTunnelBarVo;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.service.IBizTunnelBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizTunnerlBarServiceImpl extends ServiceImpl<BizTunnelBarMapper, BizTunnelBar> implements IBizTunnelBarService
{


    @Autowired
    private BizTunnelBarMapper bizTunnelBarMapper;

    @Override
    public BizTunnelBar selectEntityById(Long id) {
        return bizTunnelBarMapper.selectById(id);
    }

    @Override
    public MPage<BizTunnelBarVo> selectEntityList(BizTunnelBarDto dto, Pagination pagination) {
        MPJLambdaWrapper<BizTunnelBar> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .selectAll(BizTunnelBar.class)
                .selectAs("t1",BizTravePoint::getPointName,BizTunnelBarVo::getStartPointName)
                .selectAs("t2",BizTravePoint::getPointName,BizTunnelBarVo::getEndPointName)
                .selectAs(BizWorkface::getWorkfaceName,BizTunnelBarVo::getWorkfaceName)
                .selectAs(TunnelEntity::getTunnelName,BizTunnelBarVo::getTunnelName)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizTunnelBar::getStartPointId)
                .leftJoin(BizTravePoint.class,BizTravePoint::getPointId,BizTunnelBar::getEndPointId)
                .leftJoin(TunnelEntity.class,TunnelEntity::getTunnelId,BizTunnelBar::getTunnelId)
                .leftJoin(BizWorkface.class,BizWorkface::getWorkfaceId,BizTunnelBar::getWorkfaceId)
                .eq(dto.getWorkfaceId() != null , BizTunnelBar::getWorkfaceId,dto.getWorkfaceId())
                .eq(dto.getTunnelId() != null, BizTunnelBar::getTunnelId, dto.getTunnelId())
                .eq(StrUtil.isNotEmpty(dto.getType()), BizTunnelBar::getType, dto.getType());
        IPage<BizTunnelBarVo> list = bizTunnelBarMapper.selectJoinPage(pagination,BizTunnelBarVo.class,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(BizTunnelBarDto dto) {
        BizTunnelBar BizTunnelBar = new BizTunnelBar();
        BeanUtil.copyProperties(dto, BizTunnelBar);
//        double A = new BigDecimal(dto.getEndLat()).subtract(new BigDecimal(dto.getStartLat())).doubleValue();
//        double B = new BigDecimal(dto.getStartLon()).subtract(new BigDecimal(dto.getEndLon())).doubleValue();
//        BigDecimal c1 = new BigDecimal(dto.getEndLon()).multiply(new BigDecimal(dto.getStartLat()));
//        BigDecimal c2 = new BigDecimal(dto.getStartLon()).multiply(new BigDecimal(dto.getEndLat()));
//        double C = c1.subtract(c2).doubleValue();
//        BizTunnelBar.setA(A).setB(B).setC(C);
        return bizTunnelBarMapper.insert(BizTunnelBar);
    }

    @Override
    public int updateEntity(BizTunnelBarDto dto) {
        BizTunnelBar bizTunnelBar = new BizTunnelBar();
        BeanUtil.copyProperties(dto, bizTunnelBar);
        double A = new BigDecimal(dto.getEndLat()).subtract(new BigDecimal(dto.getStartLat())).doubleValue();
        double B = new BigDecimal(dto.getStartLon()).subtract(new BigDecimal(dto.getEndLon())).doubleValue();
        BigDecimal c1 = new BigDecimal(dto.getEndLon()).multiply(new BigDecimal(dto.getStartLat()));
        BigDecimal c2 = new BigDecimal(dto.getStartLon()).multiply(new BigDecimal(dto.getEndLat()));
        double C = c1.subtract(c2).doubleValue();
        bizTunnelBar.setA(A).setB(B).setC(C);
        return bizTunnelBarMapper.updateById(bizTunnelBar);
    }


}

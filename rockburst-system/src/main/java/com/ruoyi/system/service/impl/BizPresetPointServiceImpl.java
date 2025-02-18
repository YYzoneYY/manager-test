package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizPresetPoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.dto.BizPresetPointDto;
import com.ruoyi.system.mapper.BizPresetPointMapper;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.service.IBizPresetPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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
    public boolean setPlanPrePoint(Long planId, List<BizPresetPointDto> dtos) {
        return true;
    }

    @Override
    public int savebarPresetPoint(BizPresetPoint dto) {
        //先存生产帮,再存非生产帮
        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BizTunnelBar::getTunnelId, dto.getTunnelId());
        List<BizTunnelBar> tunnelBars = bizTunnelBarMapper.selectList(queryWrapper);
        for (BizTunnelBar tunnelBar : tunnelBars) {
            BizPresetPoint point = sssss(tunnelBar.getRange(),tunnelBar.getDirectAngle(),dto);
            point.setTunnelBarId(tunnelBar.getBarId());
            this.save(point);
        }
        return 0;
    }


    public BizPresetPoint sssss(Double x,Integer jio,BizPresetPoint point){
        BigDecimal lonMove =  new BigDecimal(Math.sin(Math.toRadians(jio))).multiply(new BigDecimal(x));
        BigDecimal latMove =  new BigDecimal(Math.cos(Math.toRadians(jio))).multiply(new BigDecimal(x));
        point.setLatitudet(new BigDecimal(point.getLatitudet()).add(latMove)+"");
        point.setLongitudet(new BigDecimal(point.getLongitudet()).add(lonMove)+"");
        return point;
    }
}

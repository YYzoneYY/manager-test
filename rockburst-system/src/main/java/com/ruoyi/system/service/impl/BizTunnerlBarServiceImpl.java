package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.dto.BizTunnelBarDto;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.service.IBizTunnelBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public MPage<BizTunnelBar> selectEntityList(BizTunnelBarDto dto, Pagination pagination) {
        QueryWrapper<BizTunnelBar> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(dto.getTunnelId() != null, BizTunnelBar::getTunnelId, dto.getTunnelId())
                .eq(StrUtil.isNotEmpty(dto.getType()), BizTunnelBar::getType, dto.getType());
        IPage<BizTunnelBar> list = bizTunnelBarMapper.selectPage(pagination,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(BizTunnelBarDto dto) {
        BizTunnelBar BizTunnelBar = new BizTunnelBar();
        BeanUtil.copyProperties(dto, BizTunnelBar);
        return bizTunnelBarMapper.insert(BizTunnelBar);
    }

    @Override
    public int updateEntity(BizTunnelBarDto dto) {
        BizTunnelBar BizTunnelBar = new BizTunnelBar();
        BeanUtil.copyProperties(dto, BizTunnelBar);
        return bizTunnelBarMapper.updateById(BizTunnelBar);
    }


}

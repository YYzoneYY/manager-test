package com.ruoyi.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizDangerArea;
import com.ruoyi.system.domain.dto.BizDangerAreaDto;
import com.ruoyi.system.mapper.BizDangerAreaMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.IBizDangerAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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



    @Override
    public BizDangerArea selectEntityById(Long id) {
        return bizDangerAreaMapper.selectById(id);
    }

    @Override
    public int initPresetPoint(Long workfaceId) {
        return 0;
    }

    @Override
    public MPage<BizDangerArea> selectEntityList(BizDangerAreaDto dto, Pagination pagination) {
        QueryWrapper<BizDangerArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(dto.getDangerAreaId() != null, BizDangerArea::getWorkfaceId, dto.getWorkfaceId())
                .eq(StrUtil.isNotEmpty(dto.getLevel()), BizDangerArea::getLevel, dto.getLevel());
        IPage<BizDangerArea> list = bizDangerAreaMapper.selectPage(pagination,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(BizDangerAreaDto dto) {
        BizDangerArea bizDangerArea = new BizDangerArea();
        BeanUtil.copyProperties(dto, bizDangerArea);
        return bizDangerAreaMapper.insert(bizDangerArea);
    }

    @Override
    public int updateEntity(BizDangerAreaDto dto) {
        BizDangerArea bizDangerArea = new BizDangerArea();
        BeanUtil.copyProperties(dto, bizDangerArea);
        return bizDangerAreaMapper.updateById(bizDangerArea);
    }



}

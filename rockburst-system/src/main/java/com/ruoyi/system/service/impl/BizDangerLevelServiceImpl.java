package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.BizDangerLevel;
import com.ruoyi.system.mapper.BizDangerLevelMapper;
import com.ruoyi.system.service.IBizDangerLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class BizDangerLevelServiceImpl extends ServiceImpl<BizDangerLevelMapper, BizDangerLevel> implements IBizDangerLevelService
{


    @Autowired
    private BizDangerLevelMapper bizDangerLevelMapper;

    @Override
    public BizDangerLevel selectEntityById(Long id) {
        return bizDangerLevelMapper.selectById(id);
    }

    @Override
    public MPage<BizDangerLevel> selectEntityList(BizDangerLevel dto, Pagination pagination) {
        QueryWrapper<BizDangerLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StrUtil.isNotEmpty(dto.getColor()), BizDangerLevel::getColor, dto.getColor())
                .like(StrUtil.isNotEmpty(dto.getName()), BizDangerLevel::getName, dto.getName())
                .eq(StrUtil.isNotEmpty(dto.getLevel()), BizDangerLevel::getLevel, dto.getLevel());
        IPage<BizDangerLevel> list = bizDangerLevelMapper.selectPage(pagination,queryWrapper);
        return new MPage<>(list);
    }

    @Override
    public int insertEntity(BizDangerLevel dto) {
        return bizDangerLevelMapper.insert(dto);
    }

    @Override
    public int updateEntity(BizDangerLevel dto) {
        return bizDangerLevelMapper.updateById(dto);
    }
}

package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.YtFactor;
import com.ruoyi.system.mapper.YtFactorMapper;
import com.ruoyi.system.service.IYtFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * 
 * @author ruoyi
 * @date 2024-11-11
 */
@Service
public class YtFactorServiceImpl extends ServiceImpl<YtFactorMapper, YtFactor> implements IYtFactorService
{
    @Autowired
    private YtFactorMapper ytFactorMapper;

    @Override
    public MPage<YtFactor> selectEntityList(YtFactor dto, Pagination pagination) {
        QueryWrapper<YtFactor> queryWrapper = new QueryWrapper<>();
        IPage<YtFactor> list = ytFactorMapper.selectPage(pagination,queryWrapper);
        return new MPage<>(list);
    }

}

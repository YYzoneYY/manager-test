package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.page.MPage;
import com.ruoyi.common.core.page.Pagination;
import com.ruoyi.system.domain.Entity.PlanAlarm;
import com.ruoyi.system.domain.dto.BizMineDto;
import com.ruoyi.system.mapper.PlanAlarmMapper;
import com.ruoyi.system.service.PlanAlarmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author:
 * @date: 2025/2/17
 * @description:
 */

@Transactional
@Service
public class PlanAlarmServiceImpl extends ServiceImpl<PlanAlarmMapper, PlanAlarm> implements PlanAlarmService {

    @Resource
    private PlanAlarmMapper planAlarmMapper;

    @Override
    public MPage<PlanAlarm> selectPageList(BizMineDto bizMine, Pagination pagination) {
        IPage<PlanAlarm> list = planAlarmMapper.selectPage(pagination,new QueryWrapper<PlanAlarm>());
        return new MPage<>(list);
    }
}
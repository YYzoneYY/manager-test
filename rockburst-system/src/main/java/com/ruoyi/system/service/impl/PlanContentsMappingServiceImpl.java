package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.PlanContentsMappingEntity;
import com.ruoyi.system.mapper.PlanContentsMappingMapper;
import com.ruoyi.system.service.PlanContentsMappingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2024/12/11
 * @description:
 */

@Service
@Transactional
public class PlanContentsMappingServiceImpl extends ServiceImpl<PlanContentsMappingMapper, PlanContentsMappingEntity> implements PlanContentsMappingService {

    @Resource
    private PlanContentsMappingMapper planContentsMappingMapper;

}
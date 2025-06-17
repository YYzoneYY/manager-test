package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.DrillMappingEntity;
import com.ruoyi.system.mapper.DrillMappingMapper;
import com.ruoyi.system.service.DrillMappingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Service
@Transactional
public class DrillMappingServiceImpl extends ServiceImpl<DrillMappingMapper, DrillMappingEntity> implements DrillMappingService {

    @Resource
    private DrillMappingMapper drillMappingMapper;
}
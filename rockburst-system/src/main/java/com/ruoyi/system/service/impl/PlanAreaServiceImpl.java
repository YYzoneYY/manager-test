package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.PlanAreaEntity;
import com.ruoyi.system.mapper.PlanAreaMapper;
import com.ruoyi.system.service.PlanAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */

@Transactional
@Service
public class PlanAreaServiceImpl extends ServiceImpl<PlanAreaMapper, PlanAreaEntity> implements PlanAreaService {
}
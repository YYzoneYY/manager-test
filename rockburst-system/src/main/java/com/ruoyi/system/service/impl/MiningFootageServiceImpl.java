package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.mapper.MiningFootageMapper;
import com.ruoyi.system.service.MiningFootageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Service
@Transactional
public class MiningFootageServiceImpl extends ServiceImpl<MiningFootageMapper, MiningFootageEntity> implements MiningFootageService{
}
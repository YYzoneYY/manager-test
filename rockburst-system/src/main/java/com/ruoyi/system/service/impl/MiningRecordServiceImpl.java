package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.mapper.MiningRecordMapper;
import com.ruoyi.system.service.MiningRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Service
@Transactional
public class MiningRecordServiceImpl extends ServiceImpl<MiningRecordMapper, MiningRecordEntity> implements MiningRecordService {
}
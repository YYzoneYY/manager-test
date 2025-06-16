package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.AlarmRecordEntity;
import com.ruoyi.system.mapper.AlarmRecordMapper;
import com.ruoyi.system.service.AlarmRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: shikai
 * @date: 2025/6/13
 * @description:
 */

@Service
@Transactional
public class AlarmRecordServiceImpl extends ServiceImpl<AlarmRecordMapper, AlarmRecordEntity> implements AlarmRecordService {
}
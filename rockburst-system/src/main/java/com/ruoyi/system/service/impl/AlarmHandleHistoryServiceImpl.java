package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.AlarmHandleHistoryEntity;
import com.ruoyi.system.mapper.AlarmHandleHistoryMapper;
import com.ruoyi.system.service.AlarmHandleHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: shikai
 * @date: 2025/6/18
 * @description:
 */

@Service
@Transactional
public class AlarmHandleHistoryServiceImpl extends ServiceImpl<AlarmHandleHistoryMapper, AlarmHandleHistoryEntity> implements AlarmHandleHistoryService {

    @Resource
    private AlarmHandleHistoryMapper alarmHandleHistoryMapper;
}
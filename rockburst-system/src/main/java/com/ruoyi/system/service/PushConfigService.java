package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.PushConfigEntity;
import com.ruoyi.system.domain.dto.PushConfigDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/7/2
 * @description:
 */
public interface PushConfigService extends IService<PushConfigEntity> {

    boolean batchInsert(List<PushConfigDTO> pushConfigDTOS);
}
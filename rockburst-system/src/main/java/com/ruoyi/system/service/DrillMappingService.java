package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.DrillMappingEntity;
import com.ruoyi.system.domain.dto.DrillPropertiesDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */
public interface DrillMappingService extends IService<DrillMappingEntity> {

    List<DrillPropertiesDTO> getDrillProperties(Long geologyDrillId);
}
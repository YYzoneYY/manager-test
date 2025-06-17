package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.GeologyDrillEntity;
import com.ruoyi.system.domain.dto.GeologyDrillDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */
public interface GeologyDrillService extends IService<GeologyDrillEntity> {

    boolean batchInsert(List<GeologyDrillDTO> geologyDrillDTOList);
}
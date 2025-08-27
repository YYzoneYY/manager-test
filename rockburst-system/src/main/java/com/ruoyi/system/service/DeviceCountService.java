package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.DeviceCountDTO;
import com.ruoyi.system.domain.dto.QuantityDTO;
import com.ruoyi.system.domain.dto.SenSorCountDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/26
 * @description:
 */
public interface DeviceCountService {

    DeviceCountDTO obtainDeviceCount(Long mineId);

    List<QuantityDTO> obtainDeviceCountByType(Long mineId);

    List<SenSorCountDTO> obtainSenSorCount(Long mineId);


}
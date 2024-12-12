package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.RelatesInfoEntity;
import com.ruoyi.system.domain.dto.RelatesInfoDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/11
 * @description:
 */
public interface RelatesInfoService extends IService<RelatesInfoEntity> {

    void insert(Long planId, String planType, String type, List<RelatesInfoDTO> relatesInfoDTOS);

    boolean deleteById(List<Long> planIdList);

    List<RelatesInfoDTO> getByPlanId(Long planId);
}
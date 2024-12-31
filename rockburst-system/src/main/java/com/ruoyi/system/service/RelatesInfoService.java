package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.RelatesInfoEntity;
import com.ruoyi.system.domain.dto.PointDTO;
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

    /**
     * 获取计划中已使用的导线点
     * @param planType 计划类型
     * @param type 类型
     * @param tunnelId 巷道id
     * @return 导线点集合
     */
    List<Long> getTraversePoint(String planType, String type, Long tunnelId);
}
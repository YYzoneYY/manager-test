package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.PlanAreaEntity;
import com.ruoyi.system.domain.dto.PlanAreaDTO;
import com.ruoyi.system.domain.dto.TraversePointGatherDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */
public interface PlanAreaService extends IService<PlanAreaEntity> {

    void insert(Long planId, List<PlanAreaDTO> planAreaDTOS, List<TraversePointGatherDTO> traversePointGatherDTOS);

    boolean deleteById(List<Long> planIdList);

    List<PlanAreaDTO> getByPlanId(Long planId);
}
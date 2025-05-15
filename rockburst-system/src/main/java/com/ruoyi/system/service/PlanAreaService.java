package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.PlanAreaEntity;
import com.ruoyi.system.domain.dto.PlanAreaBatchDTO;
import com.ruoyi.system.domain.dto.PlanAreaDTO;
import com.ruoyi.system.domain.dto.TraversePointGatherDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */
public interface PlanAreaService extends IService<PlanAreaEntity> {

    boolean insert(Long planId, Long workFaceId, String type, List<PlanAreaDTO> planAreaDTOS, List<TraversePointGatherDTO> traversePointGatherDTOS);

    boolean batchInsert(List<PlanAreaBatchDTO> planAreaBatchDTOS);

    boolean deleteById(List<Long> planIdList);

    List<PlanAreaDTO> getByPlanId(Long planId);
}
package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.PlanDTO;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */
public interface PlanService extends IService<PlanEntity> {

    /**
     * 工程计划新增
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    int insertPlan(PlanDTO planDTO);

    /**
     * 工程计划修改
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    int updatePlan(PlanDTO planDTO);

}
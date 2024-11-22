package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.EngineeringPlanEntity;
import com.ruoyi.system.domain.dto.EngineeringPlanDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */
public interface EngineeringPlanService extends IService<EngineeringPlanEntity> {

    /**
     * 工程计划新增
     * @param engineeringPlanDTO 参数DTO
     * @return 返回结果
     */
    int insertPlan(EngineeringPlanDTO engineeringPlanDTO);

    /**
     * 工程计划修改
     * @param engineeringPlanDTO 参数DTO
     * @return 返回结果
     */
    int updatePlan(EngineeringPlanDTO engineeringPlanDTO);

    /**
     * 根据id查询
     * @param engineeringPlanId 计划id
     * @return 返回结果
     */
    EngineeringPlanDTO queryById(Long engineeringPlanId);

    /**
     * 分页查询
     * @param selectPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryPage(SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize);

    /**
     * 提交审核
     * @param engineeringPlanId 计划id
     * @return 返回结果
     */
    String submitForReview(Long engineeringPlanId);
}
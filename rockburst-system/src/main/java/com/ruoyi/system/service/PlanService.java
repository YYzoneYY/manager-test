package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */
public interface PlanService extends IService<PlanEntity> {

//    /**
//     * 工程计划新增
//     * @param planDTO 参数DTO
//     * @return 返回结果
//     */
//    int insertPlan(PlanDTO planDTO);
//
//    /**
//     * 工程计划修改
//     * @param planDTO 参数DTO
//     * @return 返回结果
//     */
//    int updatePlan(PlanDTO planDTO);
//
//    /**
//     * 根据id查询
//     * @param engineeringPlanId 计划id
//     * @return 返回结果
//     */
//    PlanDTO queryById(Long engineeringPlanId);
//
//    /**
//     * 分页查询
//     * @param selectPlanDTO 查询参数DTO
//     * @param pageNum 当前页码
//     * @param pageSize 条数
//     * @return 返回结果
//     */
//    TableData queryPage(SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize);
//
//    /**
//     * 提交审核
//     * @param engineeringPlanId 计划id
//     * @return 返回结果
//     */
//    String submitForReview(Long engineeringPlanId);
//
//    /**
//     * 撤回
//     * @param engineeringPlanId 计划id
//     * @return 返回结果
//     */
//    String withdraw(Long engineeringPlanId);
//
//    /**
//     * 批量删除
//     * @param engineeringPlanIds 主键id数组
//     * @return 返回结果
//     */
//    boolean deletePlan(Long[] engineeringPlanIds);
}
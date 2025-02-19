package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.PlanPastEntity;
import com.ruoyi.system.domain.dto.PlanPastDTO;
import com.ruoyi.system.domain.dto.ProjectWarnChoiceListDTO;
import com.ruoyi.system.domain.dto.SelectPlanDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */
public interface PlanPastService extends IService<PlanPastEntity> {

    /**
     * 工程计划新增
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    int insertPlan(PlanPastDTO planDTO);

    /**
     * 工程计划修改
     * @param planDTO 参数DTO
     * @return 返回结果
     */
    int updatePlan(PlanPastDTO planDTO);

    /**
     * 根据id查询
     * @param planId 计划id
     * @return 返回结果
     */
    PlanPastDTO queryById(Long planId);

    /**
     * 分页查询
     * @param permission 权限
     * @param selectPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryPage(BasePermission permission, SelectPlanDTO selectPlanDTO, Integer pageNum, Integer pageSize);

    /**
     * 撤回
     * @param planId 计划id
     * @return 返回结果
     */
    String withdraw(Long planId);

    /**
     * 批量删除
     * @param planIds 主键id数组
     * @return 返回结果
     */
    boolean deletePlan(Long[] planIds);

    /**
     * 获取工程预警方案下拉列表
     * @return 返回结果
     */
    List<ProjectWarnChoiceListDTO> getProjectWarnChoiceList();

    /**
     * 根据导线点获取计划
     * @param traversePoint 导线点id
     * @param distance 距离（+-）
     * @return 返回结果
     */
    List<Long> getPlanByPoint(String traversePoint, String distance);
}
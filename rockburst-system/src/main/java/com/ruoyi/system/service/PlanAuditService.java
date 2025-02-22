package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.PlanAuditEntity;
import com.ruoyi.system.domain.dto.PlanAuditDTO;
import com.ruoyi.system.domain.dto.PlanDTO;
import com.ruoyi.system.domain.dto.SelectNewPlanDTO;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */
public interface PlanAuditService extends IService<PlanAuditEntity> {

    /**
     * 点击审核按钮
     * @param planId 计划id
     * @return 返回结果
     */
    PlanDTO audit(Long planId);

    /**
     * 审核
     * @param planAuditDTO 参数DTO
     * @return 返回结果
     */
    int addAudit(PlanAuditDTO planAuditDTO);

    /**
     * 分页查询
     * @param permission 权限
     * @param selectNewPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryPage(BasePermission permission, SelectNewPlanDTO selectNewPlanDTO, Integer pageNum, Integer pageSize);

    /**
     * 审核历史分页查询
     * @param permission 权限
     * @param selectNewPlanDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData auditHistoryPage(BasePermission permission, SelectNewPlanDTO selectNewPlanDTO, Integer pageNum, Integer pageSize);
}
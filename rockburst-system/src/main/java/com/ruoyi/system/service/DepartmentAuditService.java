package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.DepartmentAuditEntity;
import com.ruoyi.system.domain.dto.DepartAuditDTO;
import com.ruoyi.system.domain.dto.SelectDeptAuditDTO;
import com.ruoyi.system.domain.dto.SelectProjectDTO;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */
public interface DepartmentAuditService extends IService<DepartmentAuditEntity> {

    /**
     * 点击审核按钮
     * @param projectId 计划id
     * @return 返回结果
     */
    int clickAudit(Long projectId);

    /**
     * 科室审核
     * @param departAuditDTO 参数DTO
     * @return 返回结果
     */
    int departmentAudit(DepartAuditDTO departAuditDTO);

    /**
     * 分页查询
     * @param selectDeptAuditDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryByPage(SelectDeptAuditDTO selectDeptAuditDTO, Integer pageNum, Integer pageSize);
}
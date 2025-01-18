package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.domain.BasePermission;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.dto.SelectProjectDTO;
import com.ruoyi.system.domain.dto.TeamAuditDTO;
import com.ruoyi.system.domain.vo.BizProjectRecordDetailVo;

/**
 * @author: shikai
 * @date: 2024/11/25
 * @description:
 */
public interface TeamAuditService extends IService<TeamAuditEntity> {

    /**
     * 点击审核按钮
     * @param projectId 计划id
     * @return 返回结果
     */
    BizProjectRecordDetailVo audit(Long projectId);

    /**
     * 审核
     * @param teamAuditDTO 参数DTO
     * @return 返回结果
     */
    int addTeamAudit(TeamAuditDTO teamAuditDTO);

    /**
     * 分页查询
     * @param selectProjectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 条数
     * @return 返回结果
     */
    TableData queryByPage(BasePermission permission, SelectProjectDTO selectProjectDTO, Integer pageNum, Integer pageSize);
}
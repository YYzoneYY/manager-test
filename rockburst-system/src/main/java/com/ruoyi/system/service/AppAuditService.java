package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.TeamAuditEntity;
import com.ruoyi.system.domain.dto.AppAuditDetailDTO;
import com.ruoyi.system.domain.dto.SelectDTO;

/**
 * @author: shikai
 * @date: 2025/1/6
 * @description:
 */
public interface AppAuditService {

    /**
     * 待审批-分页查询(区队审核)
     * @param selectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    TableData teamAuditByPage(SelectDTO selectDTO, Integer pageNum, Integer pageSize);

    /**
     * 已审批-分页查询(区队审核)
     * @param selectDTO 查询参数DTO
     * @param userId 用户id
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    TableData teamApprovedByPage(SelectDTO selectDTO, Long userId, Integer pageNum, Integer pageSize);

    /**
     * 待审批-分页查询(科室审核)
     * @param selectDTO 查询参数DTO
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    TableData departAuditByPage(SelectDTO selectDTO, Integer pageNum, Integer pageSize);

    /**
     * 已审批-分页查询(科室审核)
     * @param selectDTO 查询参数DTO
     * @param userId 用户id
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return 返回结果
     */
    TableData departApprovedByPage(SelectDTO selectDTO, Long userId, Integer pageNum, Integer pageSize);

    /**
     * 审批详情
     * @param projectId 工程填报id
     * @param tag 审批类型标识(1-区队审核、2-科室审核)
     * @return 返回结果
     */
    AppAuditDetailDTO detail(Long projectId, String tag);

}
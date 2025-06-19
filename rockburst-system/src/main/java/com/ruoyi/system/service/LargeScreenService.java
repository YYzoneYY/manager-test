package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.largeScreen.*;

import java.util.Date;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/5/29
 * @description:
 */
public interface LargeScreenService {

    /**
     * 获取施工工程
     * @param tag 标识
     * @param select1DTO 查询参数
     * @return 结果
     */
    List<ProjectDTO> obtainProject(String tag, Select1DTO select1DTO);

    /**
     * 获取施工类型分类统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    List<ProjectTypeDTO> obtainProjectType(Date startTime, Date endTime);

    /**
     * 获取计划统计
     * @return 统计结果
     */
    List<PlanCountDTO> obtainPlanCount();

    /**
     * 获取施工钻孔树
     * @return 结果
     */
    List<SimpleTreeDTO> obtainProjectTree();

    /**
     * 通过工程Id 获取视频地址
     * @param projectId 工程id
     * @return 结果
     */
    DataDTO obtainUrl(Long projectId);

    /**
     * 获取报警记录
     * @param alarmType 报警类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 结果
     */
    List<AlarmRecordDTO> obtainAlarmRecord(String alarmType, Long startTime, Long endTime);

    /**
     * 报警处理
     * @return 结果
     */
    boolean alarmHandle(HandleDTO handleDTO);
}
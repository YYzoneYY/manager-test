package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.largeScreen.ProjectDTO;
import com.ruoyi.system.domain.dto.largeScreen.ProjectTypeDTO;
import com.ruoyi.system.domain.dto.largeScreen.Select1DTO;

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
    List<ProjectTypeDTO> obtainProjectType(Long startTime, Long endTime);
}
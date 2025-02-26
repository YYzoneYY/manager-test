package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.dto.PressureHoleImportDTO;

import java.util.Date;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/11
 * @description:
 */
public interface PressureHoleFormsService {

    /**
     * 根据时间导出卸压孔报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回结果
     */
    List<PressureHoleImportDTO> ExportPressureHoleForms(Date startTime, Date endTime);

    /**
     * 分页查询卸压孔报表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 返回结果
     */
    TableData queryPage(Date startTime, Date endTime, Integer pageNum, Integer pageSize);
}
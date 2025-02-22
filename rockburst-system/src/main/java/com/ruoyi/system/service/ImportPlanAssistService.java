package com.ruoyi.system.service;

/**
 * @author: shikai
 * @date: 2025/2/21
 * @description:
 */

import com.ruoyi.system.domain.dto.ImportPlanDTO;
import com.ruoyi.system.domain.dto.ImportPlanTwoDTO;

import javax.validation.Valid;
import java.text.ParseException;

/**
 * 计划导入辅助service
 */
public interface ImportPlanAssistService {

    int importDataAdd(ImportPlanDTO importPlanDTO) throws ParseException;

    int importDataAddTwo(ImportPlanTwoDTO importPlanTwoDTO) throws ParseException;
}
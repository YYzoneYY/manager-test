package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.dto.actual.MultipleParamPlanDTO;
import com.ruoyi.system.domain.dto.actual.WarnMessageDTO;
import com.ruoyi.system.domain.dto.actual.WarnSelectDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */
public interface WarnMessageService {

    Boolean createIndex();

    TableData warnMessagePage(WarnSelectDTO warnSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    WarnMessageDTO detail(String warnInstanceNum, Long mineId);

    TableData referenceQuantityPage(String type, String keyword, Long mineId, Integer pageNum, Integer pageSize);

    boolean saveMultipleParamPlan(List<MultipleParamPlanDTO> multipleParamPlanDTOs, String location, Long mineId);
}
package com.ruoyi.system.service;

import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.dto.actual.*;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */
public interface WarnMessageService {

    Boolean createIndex();

    int insertWarnMessage(WarnMessageDTO warnMessageDTO, Long mineId);

    TableData warnMessagePage(WarnSelectDTO warnSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    WarnMessageDTO detail(String warnInstanceNum, Long mineId);

    TableData referenceQuantityPage(String type, String keyword, Long mineId, Integer pageNum, Integer pageSize);

    boolean saveMultipleParamPlan(String warnInstanceNum, String location, List<MultipleParamPlanDTO> multipleParamPlanDTOs, Long mineId);

//    boolean updateMultipleParamPlan(String warnInstanceNum, String location, List<MultipleParamPlanDTO> multipleParamPlanDTOs, Long mineId);

    List<MultipleParamPlanVO> obtainMultipleParamPlan(String warnInstanceNum, Long mineId);

    int warnHand(String warnInstanceNum, WarnHandleDTO warnHandleDTO, Long mineId);

    int ResponseOperate(String warnInstanceNum, ResponseOperateDTO responseOperateDTO, Long mineId);

    TableData singlePointWarnInfo(SingleWarnSelectDTO singleWarnSelectDTO, String measureNum, String sensorType, Long mineId, Integer pageNum, Integer pageSize);
}
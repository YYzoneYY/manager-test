package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.MultiplePlanEntity;
import com.ruoyi.system.domain.dto.actual.MultipleParamPlanDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */
public interface MultiplePlanService extends IService<MultiplePlanEntity> {

    boolean saveBatch(List<MultipleParamPlanDTO> multipleParamPlanDTOs, String location, Long mineId);

    List<MultipleParamPlanDTO> getMultipleParamPlanList(String warnInstanceNum, Long mineId);

    boolean deleteByWarnInstanceNum(List<String> warnInstanceNums, Long mineId);
}
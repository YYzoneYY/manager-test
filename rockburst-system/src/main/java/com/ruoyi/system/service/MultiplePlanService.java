package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.MultiplePlanEntity;
import com.ruoyi.system.domain.dto.actual.MultipleParamPlanDTO;
import com.ruoyi.system.domain.dto.actual.MultipleParamPlanVO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */
public interface MultiplePlanService extends IService<MultiplePlanEntity> {

    boolean saveBatch(String warnInstanceNum, String location, List<MultipleParamPlanDTO> multipleParamPlanDTOs, Long mineId);

    boolean updateBatchById(String warnInstanceNum, String location, List<MultipleParamPlanDTO> multipleParamPlanDTOS, Long mineId);

    List<MultipleParamPlanVO> getMultipleParamPlanList(String warnInstanceNum, Long mineId);

    void deleteByWarnInstanceNum(List<String> warnInstanceNums, Long mineId);
}
package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.LaneDisplacementEntity;
import com.ruoyi.system.domain.dto.LaneDisplacementDTO;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */
public interface LaneDisplacementService extends IService<LaneDisplacementEntity> {

    int addMeasure(LaneDisplacementDTO laneDisplacementDTO, Long mineId);

    int updateMeasure(LaneDisplacementDTO laneDisplacementDTO);

    LaneDisplacementDTO detail(Long displacementId);

    TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    boolean deleteByIds(Long[] displacementIds);

    int batchEnableDisable(Long[] displacementIds);
}
package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.RoofAbscissionEntity;
import com.ruoyi.system.domain.dto.LaneDisplacementDTO;
import com.ruoyi.system.domain.dto.MeasureSelectDTO;
import com.ruoyi.system.domain.dto.RoofAbscissionDTO;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */
public interface RoofAbscissionService extends IService<RoofAbscissionEntity> {

    int addMeasure(RoofAbscissionDTO roofAbscissionDTO, Long mineId);

    int updateMeasure(RoofAbscissionDTO roofAbscissionDTO);

    RoofAbscissionDTO detail(Long roofAbscissionId);

    TableData pageQueryList(MeasureSelectDTO measureSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    boolean deleteByIds(Long[] roofAbscissionIds);

    int batchEnableDisable(Long[] roofAbscissionIds);
}
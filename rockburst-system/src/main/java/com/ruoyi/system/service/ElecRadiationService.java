package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.system.domain.Entity.ElecRadiationEntity;
import com.ruoyi.system.domain.dto.ElecRadiationDTO;
import com.ruoyi.system.domain.dto.MeasureTSelectDTO;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */
public interface ElecRadiationService extends IService<ElecRadiationEntity> {

    int addMeasure(ElecRadiationDTO elecRadiationDTO, Long mineId);

    int updateMeasure(ElecRadiationDTO elecRadiationDTO);

    ElecRadiationDTO detail(Long radiationId);

    TableData pageQueryList(MeasureTSelectDTO measureTSelectDTO, Long mineId, Integer pageNum, Integer pageSize);

    boolean deleteByIds(Long[] radiationIds);

    int batchEnableDisable(Long[] radiationIds);
}
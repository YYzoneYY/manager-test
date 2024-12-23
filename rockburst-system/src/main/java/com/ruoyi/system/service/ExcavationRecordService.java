package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.ExcavationRecordEntity;
import com.ruoyi.system.domain.dto.ExcavationFootageDTO;
import com.ruoyi.system.domain.dto.ExcavationRecordDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/12/23
 * @description:
 */
public interface ExcavationRecordService extends IService<ExcavationRecordEntity> {

    ExcavationFootageDTO insertExcavationRecord(ExcavationFootageDTO excavationFootageDTO);

    List<ExcavationRecordDTO> queryByExcavationRecordId(Long excavationFootageId);
}
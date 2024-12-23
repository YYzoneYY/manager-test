package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.MiningRecordEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;
import com.ruoyi.system.domain.dto.MiningRecordDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
public interface MiningRecordService extends IService<MiningRecordEntity> {

    MiningFootageDTO insertMiningRecord(MiningFootageDTO miningFootageDTO);

    List<MiningRecordDTO> queryByMiningRecordId(Long miningFootageId);
}
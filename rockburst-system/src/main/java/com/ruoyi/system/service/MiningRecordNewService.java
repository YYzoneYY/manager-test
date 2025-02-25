package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.MiningRecordNewEntity;
import com.ruoyi.system.domain.dto.MiningFootageNewDTO;
import com.ruoyi.system.domain.dto.MiningRecordNewDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */
public interface MiningRecordNewService extends IService<MiningRecordNewEntity> {

    int insertMiningRecordNew(MiningFootageNewDTO miningFootageNewDTO);

    List<MiningRecordNewDTO> queryByMiningRecordId(Long miningFootageId);


}
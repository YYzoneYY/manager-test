package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.MiningFootageEntity;
import com.ruoyi.system.domain.dto.MiningFootageDTO;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
public interface MiningFootageService extends IService<MiningFootageEntity> {

    /**
     * 新增回采进尺
     * @param miningFootageDTO 参数实体类
     * @return 返回结果
     */
    MiningFootageDTO insertMiningFootage(MiningFootageDTO miningFootageDTO);

    /**
     * 回采进尺修改
     * @param miningFootageDTO 参数实体类
     * @return 返回结果
     */
    MiningFootageEntity updateMiningFootage(MiningFootageDTO miningFootageDTO);
}
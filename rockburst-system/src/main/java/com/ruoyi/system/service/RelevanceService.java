package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.RelevanceEntity;
import com.ruoyi.system.domain.dto.RelevanceDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/9/3
 * @description:
 */
public interface RelevanceService extends IService<RelevanceEntity> {

    boolean addRelevance(List<RelevanceDTO> relevanceDTOS, Long mineId);
}
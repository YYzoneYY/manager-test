package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.Entity.RelevanceEntity;
import com.ruoyi.system.domain.dto.RelevanceDTO;
import com.ruoyi.system.mapper.RelevanceMapper;
import com.ruoyi.system.service.RelevanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/9/3
 * @description:
 */

@Service
@Transactional
public class RelevanceServiceImpl extends ServiceImpl<RelevanceMapper, RelevanceEntity> implements RelevanceService {

    @Resource
    private RelevanceMapper relevanceMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRelevance(List<RelevanceDTO> relevanceDTOS, Long mineId) {
        if (CollectionUtils.isEmpty(relevanceDTOS)) {
            return true;
        }
        List<RelevanceEntity> relevanceEntities = new ArrayList<>();
        for (RelevanceDTO relevanceDTO : relevanceDTOS) {
            if (relevanceDTO != null) {
                RelevanceEntity relevanceEntity = new RelevanceEntity();
                relevanceEntity.setMeasureNum(relevanceDTO.getMeasureNum());
                relevanceEntity.setSensorType(relevanceDTO.getSensorType());
                relevanceEntity.setOriginalWorkFaceName(relevanceDTO.getOriginalWorkFaceName());
                relevanceEntity.setWorkFaceId(relevanceDTO.getWorkFaceId());
                relevanceEntities.add(relevanceEntity);
            }
        }
        if (relevanceEntities.isEmpty()) {
            return true;
        }
        return this.saveBatch(relevanceEntities);
    }

}
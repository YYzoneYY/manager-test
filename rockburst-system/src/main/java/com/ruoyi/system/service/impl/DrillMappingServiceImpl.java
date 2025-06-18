package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.DrillMappingEntity;
import com.ruoyi.system.domain.dto.DrillPropertiesDTO;
import com.ruoyi.system.mapper.DrillMappingMapper;
import com.ruoyi.system.service.DrillMappingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Service
@Transactional
public class DrillMappingServiceImpl extends ServiceImpl<DrillMappingMapper, DrillMappingEntity> implements DrillMappingService {

    @Resource
    private DrillMappingMapper drillMappingMapper;

    @Override
    public List<DrillPropertiesDTO> getDrillProperties(Long geologyDrillId) {
        List<DrillPropertiesDTO> drillPropertiesDTOS = new ArrayList<>();
        List<DrillMappingEntity> drillMappingEntities = drillMappingMapper.selectList(new LambdaQueryWrapper<DrillMappingEntity>()
                .eq(DrillMappingEntity::getGeologyDrillId, geologyDrillId));

        for (DrillMappingEntity drillMappingEntity : drillMappingEntities) {
            DrillPropertiesDTO drillPropertiesDTO = new DrillPropertiesDTO();
            BeanUtils.copyProperties(drillMappingEntity, drillPropertiesDTO);
            drillPropertiesDTOS.add(drillPropertiesDTO);
        }
        return drillPropertiesDTOS;
    }
}
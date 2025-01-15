package com.ruoyi.system.domain.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.domain.dto.GrowthRateConfigDTO;
import com.ruoyi.system.domain.dto.IncrementConfigDTO;
import com.ruoyi.system.domain.dto.ThresholdConfigDTO;
import com.ruoyi.system.domain.dto.WarnSchemeDTO;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author: shikai
 * @date: 2024/12/2
 * @description:
 */
public class ObtainWarnSchemeUtils {

    @Resource
    private WarnSchemeSeparateMapper warnSchemeSeparateMapper;

    /**
     * 获取预警方案信息
     */
    public static WarnSchemeDTO getObtainWarnScheme(String measureNum, String sensorType, Long workFaceId, WarnSchemeMapper warnSchemeMapper,
                                                 WarnSchemeSeparateMapper warnSchemeSeparateMapper) {
        WarnSchemeDTO warnSchemeDTO = new WarnSchemeDTO();

        // 获取预警方案基础信息
        WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                .eq(WarnSchemeEntity::getSceneType, sensorType)
                .eq(WarnSchemeEntity::getWorkFaceId, workFaceId)
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(warnSchemeEntity)) {
            warnSchemeDTO.setWarnSchemeId(warnSchemeEntity.getWarnSchemeId());
            warnSchemeDTO.setWarnSchemeName(warnSchemeEntity.getWarnSchemeName());
            warnSchemeDTO.setSceneType(warnSchemeEntity.getSceneType());
            warnSchemeDTO.setWorkFaceId(warnSchemeEntity.getWorkFaceId());
            warnSchemeDTO.setQuietHour(warnSchemeEntity.getQuietHour());
        }

        // 获取预警阈值配置
        List<ThresholdConfigDTO> thresholdConfigDTOList = SchemeConfigUtils.getThresholdConfig(measureNum, sensorType,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增量配置
        List<IncrementConfigDTO> incrementConfigDTOList = SchemeConfigUtils.getIncrementConfig(measureNum, sensorType,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增速配置
        List<GrowthRateConfigDTO> growthRateConfigDTOList = SchemeConfigUtils.getGrowthRateConfig(measureNum, sensorType,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        warnSchemeDTO.setThresholdConfigDTOS(thresholdConfigDTOList);
        warnSchemeDTO.setIncrementConfigDTOS(incrementConfigDTOList);
        warnSchemeDTO.setGrowthRateConfigDTOS(growthRateConfigDTOList);
        return warnSchemeDTO;
    }
}
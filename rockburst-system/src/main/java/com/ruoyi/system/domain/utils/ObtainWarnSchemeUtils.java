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

import java.util.*;
import java.util.stream.Stream;

/**
 * @author: shikai
 * @date: 2024/12/2
 * @description:
 */
public class ObtainWarnSchemeUtils {

    /**
     * 获取预警方案信息
     */
    public static WarnSchemeDTO getObtainWarnScheme(String measureNum, String sensorType, Long workFaceId, WarnSchemeMapper warnSchemeMapper,
                                                 WarnSchemeSeparateMapper warnSchemeSeparateMapper) {
        WarnSchemeDTO warnSchemeDTO = new WarnSchemeDTO();
        Map<String, Object> basicInfoMap = warnSchemeBasicInfo(warnSchemeMapper, sensorType, workFaceId);
        // 获取预警阈值配置
        List<ThresholdConfigDTO> thresholdConfigDTOList = SchemeConfigUtils.getThresholdConfig(measureNum, sensorType,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增量配置
        List<IncrementConfigDTO> incrementConfigDTOList = SchemeConfigUtils.getIncrementConfig(measureNum, sensorType,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增速配置
        List<GrowthRateConfigDTO> growthRateConfigDTOList = SchemeConfigUtils.getGrowthRateConfig(measureNum, sensorType,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        warnSchemeDTO.setWarnSchemeBasicInfoMap(basicInfoMap);
        warnSchemeDTO.setThresholdConfigDTOS(thresholdConfigDTOList);
        warnSchemeDTO.setIncrementConfigDTOS(incrementConfigDTOList);
        warnSchemeDTO.setGrowthRateConfigDTOS(growthRateConfigDTOList);
        return warnSchemeDTO;
    }

    private static Map<String, Object> warnSchemeBasicInfo(WarnSchemeMapper warnSchemeMapper, String sensorType, Long workFaceId) {
        Map<String, Object> map = new HashMap<>();
        try {
            WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                    .eq(WarnSchemeEntity::getSceneType, sensorType)
                    .eq(WarnSchemeEntity::getWorkFaceId, workFaceId)
                    .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (ObjectUtil.isNotEmpty(warnSchemeEntity)) {
                // 使用 Stream API 简化代码
                Stream.of(
                        new AbstractMap.SimpleEntry<>("warnSchemeId", warnSchemeEntity.getWarnSchemeId()),
                        new AbstractMap.SimpleEntry<>("warnSchemeName", warnSchemeEntity.getWarnSchemeName()),
                        new AbstractMap.SimpleEntry<>("workFaceId", warnSchemeEntity.getWorkFaceId()),
                        new AbstractMap.SimpleEntry<>("sceneType", warnSchemeEntity.getSceneType()),
                        new AbstractMap.SimpleEntry<>("quietHour", warnSchemeEntity.getQuietHour())
                ).forEach(entry -> map.put(entry.getKey(), entry.getValue()));
            }
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
            // 返回一个空的 Map
            return Collections.emptyMap();
        }
        return map;
    }
}
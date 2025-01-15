package com.ruoyi.system.domain.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.GrowthRateConfigDTO;
import com.ruoyi.system.domain.dto.IncrementConfigDTO;
import com.ruoyi.system.domain.dto.ThresholdConfigDTO;
import com.ruoyi.system.mapper.WarnSchemeMapper;
import com.ruoyi.system.mapper.WarnSchemeSeparateMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/11/30
 * @description:
 */
public class SchemeConfigUtils {

    /**
     * 获取阈值配置
     */
    public static List<ThresholdConfigDTO> getThresholdConfig(String measureNum, String sensorType, Long workFaceId, WarnSchemeMapper warnSchemeMapper,
                                                              WarnSchemeSeparateMapper warnSchemeSeparateMapper) {
        return getConfig(measureNum, sensorType, workFaceId, warnSchemeMapper, warnSchemeSeparateMapper, ConstantsInfo.THRESHOLD_CONFIG);
    }

    /**
     * 获取增量配置
     */
    public static List<IncrementConfigDTO> getIncrementConfig(String measureNum, String sensorType, Long workFaceId, WarnSchemeMapper warnSchemeMapper,
                                                              WarnSchemeSeparateMapper warnSchemeSeparateMapper) {
        return getConfig(measureNum, sensorType, workFaceId, warnSchemeMapper, warnSchemeSeparateMapper, ConstantsInfo.INCREMENT_CONFIG);
    }

    /**
     * 获取增速配置
     */
    public static List<GrowthRateConfigDTO> getGrowthRateConfig(String measureNum, String sensorType, Long workFaceId, WarnSchemeMapper warnSchemeMapper,
                                                                WarnSchemeSeparateMapper warnSchemeSeparateMapper) {
        return getConfig(measureNum, sensorType, workFaceId, warnSchemeMapper, warnSchemeSeparateMapper, ConstantsInfo.GROWTH_RATE_CONFIG);
    }

    /**
     * 公共配置获取方法
     */
    private static <T> List<T> getConfig(String measureNum, String sensorType, Long workFaceId, WarnSchemeMapper warnSchemeMapper,
                                         WarnSchemeSeparateMapper warnSchemeSeparateMapper, String configType) {
        List<T> configDTOS = new ArrayList<>();

        WarnSchemeSeparateEntity warnSchemeSeparateEntity = warnSchemeSeparateMapper.selectOne(new LambdaQueryWrapper<WarnSchemeSeparateEntity>()
                .eq(WarnSchemeSeparateEntity::getMeasureNum, measureNum)
                .eq(WarnSchemeSeparateEntity::getWorkFaceId, workFaceId)
                .eq(WarnSchemeSeparateEntity::getSceneType, sensorType)
                .eq(WarnSchemeSeparateEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));

        if (Objects.isNull(warnSchemeSeparateEntity)) {
            WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                    .eq(WarnSchemeEntity::getSceneType, sensorType)
                    .eq(WarnSchemeEntity::getWorkFaceId, workFaceId)
                    .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
            if (Objects.isNull(warnSchemeEntity)) {
                return configDTOS;
            }
            return convertToDTO(configType, warnSchemeEntity);
        }

        return convertToDTO(configType, warnSchemeSeparateEntity);
    }

    /**
     * 将配置转换为 DTO 列表
     */
    private static <T> List<T> convertToDTO(String configType, Object entity) {
        List<Map<String, Object>> mapList = null;
        if (entity instanceof WarnSchemeEntity) {
            if ("thresholdConfig".equals(configType)) {
                mapList = ((WarnSchemeEntity) entity).getThresholdConfig();
            }
            if ("incrementConfig".equals(configType)) {
                mapList = ((WarnSchemeEntity) entity).getIncrementConfig();
            }
            if ("growthRateConfig".equals(configType)) {
                mapList = ((WarnSchemeEntity) entity).getGrowthRateConfig();
            }
        } else if (entity instanceof WarnSchemeSeparateEntity) {
            if ("thresholdConfig".equals(configType)) {
                mapList = ((WarnSchemeSeparateEntity) entity).getThresholdConfig();
            } else if ("incrementConfig".equals(configType)) {
                mapList = ((WarnSchemeSeparateEntity) entity).getIncrementConfig();
            } else if ("growthRateConfig".equals(configType)) {
                mapList = ((WarnSchemeSeparateEntity) entity).getGrowthRateConfig();
            }
        }

        if (mapList == null || mapList.isEmpty()) {
            return new ArrayList<>();
        }
        if ("thresholdConfig".equals(configType)) {
            return (List<T>) mapList.stream().map(map -> {
                ThresholdConfigDTO thresholdConfigDTO = new ThresholdConfigDTO();
                thresholdConfigDTO.setCompareType(Objects.toString(map.get("compareType"), ""));
                thresholdConfigDTO.setGrade(Objects.toString(map.get("grade"), ""));
                thresholdConfigDTO.setUnit(Objects.toString(map.get("unit"), ""));
                thresholdConfigDTO.setNumberValue(new BigDecimal(Objects.toString(map.get("numberValue"), "0")));
                return thresholdConfigDTO;
            }).collect(Collectors.toList());
        } else if ("incrementConfig".equals(configType)) {
            return (List<T>) mapList.stream().map(map -> {
                IncrementConfigDTO incrementConfigDTO = new IncrementConfigDTO();
                incrementConfigDTO.setDuration(Objects.toString(map.get("duration"), ""));
                incrementConfigDTO.setCompareType(Objects.toString(map.get("compareType"), ""));
                incrementConfigDTO.setGrade(Objects.toString(map.get("grade"), ""));
                incrementConfigDTO.setUnit(Objects.toString(map.get("unit"), ""));
                incrementConfigDTO.setNumberValue(Objects.toString(map.get("numberValue"), ""));
                return incrementConfigDTO;
            }).collect(Collectors.toList());
        } else if ("growthRateConfig".equals(configType)) {
            return (List<T>) mapList.stream().map(map -> {
                GrowthRateConfigDTO growthRateConfigDTO = new GrowthRateConfigDTO();
                growthRateConfigDTO.setDuration(Objects.toString(map.get("duration"), ""));
                growthRateConfigDTO.setCompareType(Objects.toString(map.get("compareType"), ""));
                growthRateConfigDTO.setGrade(Objects.toString(map.get("grade"), ""));
                growthRateConfigDTO.setUnit(Objects.toString(map.get("unit"), ""));
                growthRateConfigDTO.setNumberValue(Objects.toString(map.get("numberValue"), ""));
                return growthRateConfigDTO;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
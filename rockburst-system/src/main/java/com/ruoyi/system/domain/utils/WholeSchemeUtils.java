package com.ruoyi.system.domain.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.domain.Entity.WarnSchemeEntity;
import com.ruoyi.system.domain.Entity.WarnSchemeSeparateEntity;
import com.ruoyi.system.domain.dto.GrowthRateConfigDTO;
import com.ruoyi.system.domain.dto.IncrementConfigDTO;
import com.ruoyi.system.domain.dto.ThresholdConfigDTO;
import com.ruoyi.system.mapper.WarnSchemeMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/8/11
 * @description:
 */
public class WholeSchemeUtils {

    public static List<ThresholdConfigDTO> getThresholdConfig(Long warnSchemeId, WarnSchemeMapper warnSchemeMapper) {
        return getConfig(warnSchemeId, warnSchemeMapper, ConstantsInfo.THRESHOLD_CONFIG);
    }

    public static List<IncrementConfigDTO> getIncrementConfig(Long warnSchemeId, WarnSchemeMapper warnSchemeMapper) {
        return getConfig(warnSchemeId, warnSchemeMapper, ConstantsInfo.INCREMENT_CONFIG);
    }

    public static List<GrowthRateConfigDTO> getGrowthRateConfig(Long warnSchemeId, WarnSchemeMapper warnSchemeMapper) {
        return getConfig(warnSchemeId, warnSchemeMapper, ConstantsInfo.GROWTH_RATE_CONFIG);
    }

    private static <T> List<T> getConfig(Long warnSchemeId, WarnSchemeMapper warnSchemeMapper, String configType) {
        List<T> configDTOS = new ArrayList<>();

        WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                        .eq(WarnSchemeEntity::getWarnSchemeId, warnSchemeId)
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (Objects.isNull(warnSchemeEntity)) {
            return configDTOS;
        }
        return convertToDTO(configType, warnSchemeEntity);
    }

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
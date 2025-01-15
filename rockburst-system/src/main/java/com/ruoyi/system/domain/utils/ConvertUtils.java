package com.ruoyi.system.domain.utils;

import com.ruoyi.system.domain.dto.GrowthRateConfigDTO;
import com.ruoyi.system.domain.dto.IncrementConfigDTO;
import com.ruoyi.system.domain.dto.ThresholdConfigDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/1/15
 * @description:
 */
public class ConvertUtils {

    //生成的日志
    private static final Logger logger = LoggerFactory.getLogger("convert-log");

    public static List<Map<String, Object>> convertThresholdMap(List<ThresholdConfigDTO> thresholdConfigDTOS) {
        return thresholdConfigDTOS.stream()
                    .map(thresholdConfigDTO -> {
                        try {
                            return new HashMap<String, Object>() {{
                                put("numberValue", Objects.requireNonNullElse(thresholdConfigDTO.getNumberValue(), ""));
                                put("unit", Objects.requireNonNullElse(thresholdConfigDTO.getUnit(), ""));
                                put("grade", Objects.requireNonNullElse(thresholdConfigDTO.getGrade(), ""));
                                put("compareType", Objects.requireNonNullElse(thresholdConfigDTO.getCompareType(), ""));
                            }};
                        } catch (Exception e) {
                            // 记录日志
                            logger.error("Error processing thresholdConfigDTO: " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    public static List<Map<String, Object>> convertIncrementMap(List<IncrementConfigDTO> incrementConfigDTOS) {
        return incrementConfigDTOS.stream()
                .map(incrementConfigDTO -> {
                    try {
                        return new HashMap<String, Object>() {{
                            put("duration", Objects.requireNonNullElse(incrementConfigDTO.getDuration(), ""));
                            put("numberValue", Objects.requireNonNullElse(incrementConfigDTO.getNumberValue(), ""));
                            put("unit", Objects.requireNonNullElse(incrementConfigDTO.getUnit(), ""));
                            put("compareType", Objects.requireNonNullElse(incrementConfigDTO.getCompareType(), ""));
                            put("grade", Objects.requireNonNullElse(incrementConfigDTO.getGrade(), ""));
                        }};
                    } catch (Exception e) {
                        // 记录日志
                        logger.error("Error processing incrementConfigDTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Map<String, Object>> convertGrowthRateMap(List<GrowthRateConfigDTO> growthRateConfigDTOS) {
        return growthRateConfigDTOS.stream()
                .map(growthRateConfigDTO -> {
                    try {
                        return new HashMap<String, Object>() {{
                            put("duration", Objects.requireNonNullElse(growthRateConfigDTO.getDuration(), ""));
                            put("numberValue", Objects.requireNonNullElse(growthRateConfigDTO.getNumberValue(), ""));
                            put("unit", Objects.requireNonNullElse(growthRateConfigDTO.getUnit(), ""));
                            put("compareType", Objects.requireNonNullElse(growthRateConfigDTO.getCompareType(), ""));
                            put("grade", Objects.requireNonNullElse(growthRateConfigDTO.getGrade(), ""));
                        }};
                    } catch (Exception e) {
                        // 记录日志
                        logger.error("Error processing growthRateConfigDTO: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
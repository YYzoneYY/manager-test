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
        String sensorTypeFmt = "";
        Map<String, String> sensorTypeMap = new HashMap<String, String>() {{
            put(ConstantsInfo.SUPPORT_RESISTANCE_TYPE, ConstantsInfo.SUPPORT);
            put(ConstantsInfo.DRILL_STRESS_TYPE, ConstantsInfo.DRILL);
            put(ConstantsInfo.ANCHOR_STRESS_TYPE, ConstantsInfo.ANCHOR_SCENE_TYPE);
            put(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE, ConstantsInfo.ANCHOR_CABLE_SCENE_TYPE);
            put(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE, ConstantsInfo.ROOF_SCENE_TYPE);
            put(ConstantsInfo.LANE_DISPLACEMENT_TYPE, ConstantsInfo.LANE_SCENE_TYPE);
            put(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE, ConstantsInfo.ELECTROMAGNETIC_SCENE_TYPE);
        }};

        if (sensorTypeMap.containsKey(sensorType)) {
            sensorTypeFmt = sensorTypeMap.get(sensorType);
        }

        // 获取预警方案基础信息
        WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                .eq(WarnSchemeEntity::getSceneType, sensorTypeFmt)
                .eq(WarnSchemeEntity::getWorkFaceId, workFaceId)
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(warnSchemeEntity)) {
            warnSchemeDTO.setWarnSchemeId(warnSchemeEntity.getWarnSchemeId());
            warnSchemeDTO.setSchemeName(warnSchemeEntity.getSchemeName());
            warnSchemeDTO.setSceneType(warnSchemeEntity.getSceneType());
            warnSchemeDTO.setWorkFaceId(warnSchemeEntity.getWorkFaceId());
            warnSchemeDTO.setQuietHour(warnSchemeEntity.getQuietHour());
        }

        // 获取预警阈值配置
        List<ThresholdConfigDTO> thresholdConfigDTOList = SchemeConfigUtils.getThresholdConfig(measureNum, sensorTypeFmt,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增量配置
        List<IncrementConfigDTO> incrementConfigDTOList = SchemeConfigUtils.getIncrementConfig(measureNum, sensorTypeFmt,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增速配置
        List<GrowthRateConfigDTO> growthRateConfigDTOList = SchemeConfigUtils.getGrowthRateConfig(measureNum, sensorTypeFmt,
                workFaceId, warnSchemeMapper, warnSchemeSeparateMapper);
        warnSchemeDTO.setThresholdConfigDTOS(thresholdConfigDTOList);
        warnSchemeDTO.setIncrementConfigDTOS(incrementConfigDTOList);
        warnSchemeDTO.setGrowthRateConfigDTOS(growthRateConfigDTOList);
        return warnSchemeDTO;
    }


    public static WarnSchemeDTO getObtainWarnSchemeT(String measureNum, String sensorType, String mark, WarnSchemeMapper warnSchemeMapper,
                                                    WarnSchemeSeparateMapper warnSchemeSeparateMapper) {
        WarnSchemeDTO warnSchemeDTO = new WarnSchemeDTO();

        String sensorTypeFmt = "";
        Map<String, String> sensorTypeMap = new HashMap<String, String>() {{
            put(ConstantsInfo.SUPPORT_RESISTANCE_TYPE, ConstantsInfo.SUPPORT);
            put(ConstantsInfo.DRILL_STRESS_TYPE, ConstantsInfo.DRILL);
            put(ConstantsInfo.ANCHOR_STRESS_TYPE, ConstantsInfo.ANCHOR_SCENE_TYPE);
            put(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE, ConstantsInfo.ANCHOR_CABLE_SCENE_TYPE);
            put(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE, ConstantsInfo.ROOF_SCENE_TYPE);
            put(ConstantsInfo.LANE_DISPLACEMENT_TYPE, ConstantsInfo.LANE_SCENE_TYPE);
            put(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE, ConstantsInfo.ELECTROMAGNETIC_SCENE_TYPE);
        }};

        if (sensorTypeMap.containsKey(sensorType)) {
            sensorTypeFmt = sensorTypeMap.get(sensorType);
        }

        // 获取预警方案基础信息
        WarnSchemeEntity warnSchemeEntity = warnSchemeMapper.selectOne(new LambdaQueryWrapper<WarnSchemeEntity>()
                .eq(WarnSchemeEntity::getSceneType, sensorTypeFmt)
                .eq(WarnSchemeEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNotNull(warnSchemeEntity)) {
            warnSchemeDTO.setWarnSchemeId(warnSchemeEntity.getWarnSchemeId());
            warnSchemeDTO.setSchemeName(warnSchemeEntity.getSchemeName());
            warnSchemeDTO.setSceneType(warnSchemeEntity.getSceneType());
            warnSchemeDTO.setWorkFaceId(warnSchemeEntity.getWorkFaceId());
            warnSchemeDTO.setQuietHour(warnSchemeEntity.getQuietHour());
        }

        // 获取预警阈值配置
        List<ThresholdConfigDTO> thresholdConfigDTOList = SchemeConfigUtils.getThresholdConfigT(measureNum, sensorTypeFmt, mark,
                warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增量配置
        List<IncrementConfigDTO> incrementConfigDTOList = SchemeConfigUtils.getIncrementConfigT(measureNum, sensorTypeFmt, mark,
                warnSchemeMapper, warnSchemeSeparateMapper);
        // 获取预警增速配置
        List<GrowthRateConfigDTO> growthRateConfigDTOList = SchemeConfigUtils.getGrowthRateConfigT(measureNum, sensorTypeFmt, mark,
                warnSchemeMapper, warnSchemeSeparateMapper);
        warnSchemeDTO.setThresholdConfigDTOS(thresholdConfigDTOList);
        warnSchemeDTO.setIncrementConfigDTOS(incrementConfigDTOList);
        warnSchemeDTO.setGrowthRateConfigDTOS(growthRateConfigDTOList);
        return warnSchemeDTO;
    }
}
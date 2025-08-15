package com.ruoyi.system.domain.utils;

import com.ruoyi.system.domain.EsEntity.MeasureActualEntity;
import com.ruoyi.system.domain.dto.actual.ActualDataDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/8/14
 * @description:
 */
public class ActualDataConverter {


    /**
     * 将MeasureActualEntity转换为ActualDataDTO
     */
    public static ActualDataDTO convertToDTO(MeasureActualEntity entity) {
        if (entity == null) {
            return null;
        }

        ActualDataDTO dto = new ActualDataDTO();
        dto.setMeasureNum(entity.getMeasureNum());
        dto.setSensorType(entity.getSensorType());
        dto.setSensorLocation(entity.getSensorLocation());
        dto.setMonitoringValue(entity.getMonitoringValue());
        dto.setMonitoringStatus(entity.getMonitoringStatus());
        dto.setDataTime(entity.getDataTime());
        // surveyAreaName和sensorName需要从其他地方获取，暂时留空
        return dto;
    }

    /**
     * 将MeasureActualEntity转换为ActualDataDTO（带额外信息查询功能）
     *
     * @param entity MeasureActualEntity对象
     * @param surveyAreaNameFetcher 获取监测区名称的函数
     * @param sensorNameFetcher 获取传感器名称的函数
     * @return ActualDataDTO对象
     */
    public static ActualDataDTO convertToDTO(
            MeasureActualEntity entity,
            Function<String, String> surveyAreaNameFetcher,
            Function<String, String> sensorNameFetcher) {
        if (entity == null) {
            return null;
        }

        ActualDataDTO dto = convertToDTO(entity);
        if (surveyAreaNameFetcher != null) {
            dto.setSurveyAreaName(surveyAreaNameFetcher.apply(entity.getMeasureNum()));
        }
        if (sensorNameFetcher != null) {
            dto.setSensorName(sensorNameFetcher.apply(entity.getMeasureNum()));
        }
        return dto;
    }

    /**
     * 将MeasureActualEntity列表转换为ActualDataDTO列表
     *
     * @param entityList MeasureActualEntity对象列表
     * @return ActualDataDTO对象列表
     */
    public static List<ActualDataDTO> convertToDTOList(List<MeasureActualEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(ActualDataConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将MeasureActualEntity列表转换为ActualDataDTO列表（带额外信息查询功能）
     *
     * @param entityList MeasureActualEntity对象列表
     * @param surveyAreaNameFetcher 获取监测区名称的函数
     * @param sensorNameFetcher 获取传感器名称的函数
     * @return ActualDataDTO对象列表
     */
    public static List<ActualDataDTO> convertToDTOList(
            List<MeasureActualEntity> entityList,
            Function<String, String> surveyAreaNameFetcher,
            Function<String, String> sensorNameFetcher) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(entity -> convertToDTO(entity, surveyAreaNameFetcher, sensorNameFetcher))
                .collect(Collectors.toList());
    }
}
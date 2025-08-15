package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.system.EsMapper.MeasureActualMapper;
import com.ruoyi.system.domain.Entity.DrillingStressEntity;
import com.ruoyi.system.domain.EsEntity.MeasureActualEntity;
import com.ruoyi.system.domain.dto.actual.ActualDataDTO;
import com.ruoyi.system.domain.dto.actual.ActualSelectDTO;
import com.ruoyi.system.domain.dto.actual.LineGraphDTO;
import com.ruoyi.system.domain.utils.ActualDataConverter;
import com.ruoyi.system.domain.utils.ObtainDateUtils;
import com.ruoyi.system.domain.utils.validatePageUtils;
import com.ruoyi.system.mapper.AnchorCableStressMapper;
import com.ruoyi.system.mapper.DrillingStressMapper;
import com.ruoyi.system.mapper.LaneDisplacementMapper;
import com.ruoyi.system.service.MeasureActualService;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

/**
 * @author: shikai
 * @date: 2025/8/13
 * @description:
 */

@Service
public class MeasureActualServiceImpl implements MeasureActualService {

    @Resource
    private MeasureActualMapper measureActualMapper;

    @Resource
    private DrillingStressMapper drillingStressMapper;

    @Resource
    private LaneDisplacementMapper laneDisplacementMapper;

    @Resource
    private AnchorCableStressMapper anchorCableStressMapper;

    @Override
    public Boolean createIndex() {
        boolean flag = false;
        flag = measureActualMapper.createIndex();
        return flag;
    }

    @Override
    public TableData ActualDataPage(ActualSelectDTO actualSelectDTO, List<String> sensorTypes, Long mineId,
                                           String tag, Integer pageNum, Integer pageSize) {
        if (actualSelectDTO == null) {
            throw new IllegalArgumentException("参数 actualSelectDTO 不允许为空!");
        }
        List<String> MeasureNums = new ArrayList<>();
        // 钻孔应力
        if (tag.equals("1")) {
            MeasureNums = drillingStressMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        // 巷道表面位移
        if (tag.equals("2")) {
            MeasureNums = laneDisplacementMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        // 锚杆/索应力
        if (tag.equals("3")) {
            MeasureNums = anchorCableStressMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }

        // 如果没有获取到测点编码，则直接返回空结果
        if (MeasureNums.isEmpty()) {
            TableData result = new TableData();
            result.setTotal(0L);
            result.setRows(Collections.emptyList());
            return result;
        }

        // 分页参数校验与默认值设置
        int validPageNum = validatePageUtils.validateAndSetDefaultPageNum(pageNum);
        int validPageSize = validatePageUtils.validateAndSetDefaultPageSize(pageSize);
        // 操作ES
        LambdaEsQueryWrapper<MeasureActualEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.in(MeasureActualEntity::getMeasureNum, MeasureNums)
                .in(MeasureActualEntity::getSensorType, sensorTypes)
                .eq(MeasureActualEntity::getMineId, mineId)
                .eq(StrUtil.isNotEmpty(actualSelectDTO.getMonitoringStatus()), MeasureActualEntity::getMonitoringStatus, actualSelectDTO.getMonitoringStatus())
                .between(ObjectUtil.isNotNull(actualSelectDTO.getStartTime()),
                        MeasureActualEntity::getDataTime,
                        actualSelectDTO.getStartTime(),
                        actualSelectDTO.getEndTime())
                .orderByDesc(MeasureActualEntity::getDataTime);
        EsPageInfo<MeasureActualEntity> pageInfo = measureActualMapper.pageQuery(queryWrapper, validPageNum, validPageSize);

        // 批量获取钻孔应力信息，避免N+1查询问题
        Map<String, DrillingStressEntity> drillingStressMap = new HashMap<>();
        if (pageInfo != null && pageInfo.getList() != null && !pageInfo.getList().isEmpty()) {
            // 提取所有测点编码并去重
            List<String> measureNumList = new ArrayList<>();
            for (MeasureActualEntity entity : pageInfo.getList()) {
                if (!measureNumList.contains(entity.getMeasureNum())) {
                    measureNumList.add(entity.getMeasureNum());
                }
            }

            // 一次性查询所有需要的钻孔应力信息
            List<DrillingStressEntity> drillingStressList = drillingStressMapper.selectList(
                    new LambdaQueryWrapper<DrillingStressEntity>()
                            .in(DrillingStressEntity::getMeasureNum, measureNumList)
                            .eq(DrillingStressEntity::getMineId, mineId)
                            .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                            .select(DrillingStressEntity::getMeasureNum,
                                    DrillingStressEntity::getSurveyAreaName,
                                    DrillingStressEntity::getSensorLocation)
            );

            // 构建以测点编码为键的Map，方便快速查找
            for (DrillingStressEntity entity : drillingStressList) {
                drillingStressMap.put(entity.getMeasureNum(), entity);
            }
        }

        // 使用工具类转换为ActualDataDTO列表，并提供额外信息获取函数
        List<ActualDataDTO> dtoList = ActualDataConverter.convertToDTOList(
                pageInfo != null ? pageInfo.getList() : null,
                new Function<String, String>() {
                    @Override
                    public String apply(String measureNum) {
                        // 根据测点编码获取监测区名称
                        DrillingStressEntity entity = drillingStressMap.get(measureNum);
                        return entity != null ? entity.getSurveyAreaName() : null;
                    }
                },
                new Function<String, String>() {
                    @Override
                    public String apply(String measureNum) {
                        // 根据测点编码获取传感器位置作为传感器名称
                        DrillingStressEntity entity = drillingStressMap.get(measureNum);
                        return entity != null ? entity.getSensorLocation() : null;
                    }
                });

        TableData result = new TableData();
        result.setTotal(pageInfo == null ? 0 : pageInfo.getTotal());
        result.setRows(dtoList);
        return result;
    }

    @Override
    public List<LineGraphDTO> obtainLineGraph(String measureNum, String range, Long startTime, Long endTime,
                                              List<String> sensorTypes, Long mineId) {
        checkBindParamNotNull(measureNum, range);
        long currentTimeMillis = System.currentTimeMillis();
        List<MeasureActualEntity> list = new ArrayList<>();

        // 构建基础查询条件
        LambdaEsQueryWrapper<MeasureActualEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper
                .in(MeasureActualEntity::getSensorType, sensorTypes)
                .eq(MeasureActualEntity::getMeasureNum, measureNum)
                .eq(MeasureActualEntity::getMineId, mineId);

        // 根据不同时间范围设置查询条件
        if (range.equals(ConstantsInfo.THIRTY_MINUTE_TAG)) {
            Long thirtyMinutesAgo = ObtainDateUtils.getThirtyMinutesTime(currentTimeMillis);
            queryWrapper.between(MeasureActualEntity::getDataTime, thirtyMinutesAgo, currentTimeMillis);
        } else if (range.equals(ConstantsInfo.ONE_HOUR_TAG)) {
            Long oneHourAgo = ObtainDateUtils.getOneHourTime(currentTimeMillis);
            queryWrapper.between(MeasureActualEntity::getDataTime, oneHourAgo, currentTimeMillis);
        } else if (range.equals(ConstantsInfo.TWENTY_FOUR_HOURS_TAG)) {
            Long twentyFourHoursAgo = ObtainDateUtils.getTwentyFourHoursTime(currentTimeMillis);
            queryWrapper.between(MeasureActualEntity::getDataTime, twentyFourHoursAgo, currentTimeMillis);
        } else if (range.equals(ConstantsInfo.ON_THAT_DAY_TAG)) {
            Long startOfDay = ObtainDateUtils.getCurrentZoneTime();
            Long endOfDay = ObtainDateUtils.getCurrentTwentyFourHoursTime();
            queryWrapper.between(MeasureActualEntity::getDataTime, startOfDay, endOfDay);
        } else if (range.equals(ConstantsInfo.CUSTOMIZE_TAG)) {
            if (ObjectUtil.isNull(startTime) || ObjectUtil.isNull(endTime)) {
                throw new RuntimeException("开始时间/结束时间不能为空");
            }
            queryWrapper.between(MeasureActualEntity::getDataTime, startTime, endTime);
        } else {
            throw new RuntimeException("不支持的时间范围类型: " + range);
        }

        list = measureActualMapper.selectList(queryWrapper);

        // 转换为LineGraphDTO列表
        List<LineGraphDTO> lineGraphDTOS = new ArrayList<>();
        for (MeasureActualEntity entity : list) {
            LineGraphDTO lineGraphDTO = new LineGraphDTO();
            lineGraphDTO.setSensorType(entity.getSensorType());
            lineGraphDTO.setMeasureNum(entity.getMeasureNum());
            lineGraphDTO.setMonitoringValue(entity.getMonitoringValue());
            lineGraphDTO.setDataTime(entity.getDataTime());
            lineGraphDTOS.add(lineGraphDTO);
        }

        return lineGraphDTOS;
    }



    private void checkBindParamNotNull(String measureNum, String range) {
        if (ObjectUtil.isNull(measureNum)) {
            throw new RuntimeException("测点编码不能为空");
        }
        if (ObjectUtil.isNull(range)) {
            throw new RuntimeException("时间范围不能为空");
        }
    }

}
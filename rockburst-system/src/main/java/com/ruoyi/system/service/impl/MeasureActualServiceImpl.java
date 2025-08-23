package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.page.TableData;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.EsMapper.MeasureActualMapper;
import com.ruoyi.system.EsMapper.WarnMessageMapper;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.EsEntity.MeasureActualEntity;
import com.ruoyi.system.domain.EsEntity.WarnMessageEntity;
import com.ruoyi.system.domain.dto.actual.*;
import com.ruoyi.system.domain.utils.ActualDataConverter;
import com.ruoyi.system.domain.utils.ObtainDateUtils;
import com.ruoyi.system.domain.utils.validatePageUtils;
import com.ruoyi.system.mapper.*;
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

    @Resource
    private SupportResistanceMapper supportResistanceMapper;

    @Resource
    private RoofAbscissionMapper roofAbscissionMapper;

    @Resource
    private WarnMessageMapper warnMessageMapper;

    @Override
    public Boolean createIndex() {
        boolean flag = false;
        flag = measureActualMapper.createIndex();
        return flag;
    }

    @Override
    public int insert(ActualDTO actualDTO, Long mineId) {
        int flag = 0;
        MeasureActualEntity measureActualEntity = new MeasureActualEntity();
        BeanUtils.copyProperties(actualDTO, measureActualEntity);
        measureActualEntity.setMineId(mineId);
        flag = measureActualMapper.insert(measureActualEntity);
        return flag;
    }

    @Override
    public TableData ActualDataPage(ActualSelectDTO actualSelectDTO, List<String> sensorTypes, Long mineId,
                                    String tag, Integer pageNum, Integer pageSize) {
        if (actualSelectDTO == null) {
            throw new IllegalArgumentException("参数 actualSelectDTO 不允许为空!");
        }

        // 获取测点编码列表
        List<String> measureNums = getMeasureNums(actualSelectDTO, mineId, tag);

        // 如果没有获取到测点编码，则直接返回空结果
        if (measureNums.isEmpty()) {
            TableData result = new TableData();
            result.setTotal(0L);
            result.setRows(Collections.emptyList());
            return result;
        }

        // 分页查询ES数据
        EsPageInfo<MeasureActualEntity> pageInfo = queryEsData(actualSelectDTO, sensorTypes, mineId, measureNums, pageNum, pageSize);

        // 获取实体信息映射
        Map<String, Object> entityMap = getEntityMap(pageInfo, mineId, tag);

        // 转换为DTO列表
        List<ActualDataDTO> dtoList = convertToDTOList(pageInfo, entityMap, tag);

        TableData result = new TableData();
        result.setTotal(pageInfo == null ? 0 : pageInfo.getTotal());
        result.setRows(dtoList);
        return result;
    }

    @Override
    public TableData ActualDataTPage(ActualSelectTDTO actualSelectTDTO, List<String> sensorTypes, Long mineId, Integer pageNum, Integer pageSize) {
        if (actualSelectTDTO == null) {
            throw new IllegalArgumentException("参数 actualSelectTDTO 不允许为空!");
        }

        // 分页参数校验与默认值设置
        int validPageNum = validatePageUtils.validateAndSetDefaultPageNum(pageNum);
        int validPageSize = validatePageUtils.validateAndSetDefaultPageSize(pageSize);

        // 操作ES
        LambdaEsQueryWrapper<MeasureActualEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper
                .in(MeasureActualEntity::getSensorType, sensorTypes)
                .like(StrUtil.isNotEmpty(actualSelectTDTO.getSensorLocation()), MeasureActualEntity::getSensorLocation,
                        actualSelectTDTO.getSensorLocation())
                .eq(MeasureActualEntity::getMineId, mineId)
                .between(ObjectUtil.isNotNull(actualSelectTDTO.getStartTime()),
                        MeasureActualEntity::getDataTime,
                        actualSelectTDTO.getStartTime(),
                        actualSelectTDTO.getEndTime())
                .orderByDesc(MeasureActualEntity::getDataTime);
        EsPageInfo<MeasureActualEntity> pageInfo = measureActualMapper.pageQuery(queryWrapper, validPageNum, validPageSize);
        TableData result = new TableData();
        result.setTotal(pageInfo == null ? 0 : pageInfo.getTotal());
        result.setRows(pageInfo == null ? Collections.emptyList() : pageInfo.getList());
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
        setTimeRangeCondition(queryWrapper, range, startTime, endTime, currentTimeMillis);

        list = measureActualMapper.selectList(queryWrapper);

        // 转换为LineGraphDTO列表
        List<LineGraphDTO> lineGraphDTOS = new ArrayList<>();
        for (MeasureActualEntity entity : list) {
            LineGraphDTO lineGraphDTO = new LineGraphDTO();
            lineGraphDTO.setSensorType(entity.getSensorType());
            lineGraphDTO.setMeasureNum(entity.getMeasureNum());

            // 根据不同传感器类型设置相应的值字段
            setLineGraphDTOValuesBySensorType(lineGraphDTO, entity);

            lineGraphDTO.setDataTime(entity.getDataTime());
            lineGraphDTOS.add(lineGraphDTO);
        }

        return lineGraphDTOS;
    }

    @Override
    public SingleLineChartDTO obtainSingleLineChart(String measureNum, String sensorType, Long mineId) {
        if (ObjectUtil.isNull(measureNum)) {
            throw new RuntimeException("测点编码不能为空！");
        }
        if (ObjectUtil.isNull(sensorType)) {
            throw new RuntimeException("传感器类型不能为空！");
        }

        LambdaEsQueryWrapper<WarnMessageEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.eq(WarnMessageEntity::getMeasureNum, measureNum)
                .eq(WarnMessageEntity::getSensorType, sensorType)
                .eq(WarnMessageEntity::getMineId, mineId);
        WarnMessageEntity warnMessageEntity = warnMessageMapper.selectOne(queryWrapper);

        if (ObjectUtil.isNull(warnMessageEntity)) {
            throw new RuntimeException("未找到对应的预警信息！");
        }

        long startTime = ObtainDateUtils.getThirtyMinutesTime(warnMessageEntity.getStartTime());
        long endTime = 0L;

        Long warnET = warnMessageEntity.getEndTime();
        if (ObjectUtil.isNull(warnET)) {
            endTime = System.currentTimeMillis();
        } else {
            Long afterTime = ObtainDateUtils.getThirtyMinutesAfterTime(warnET);
            boolean isAfterTimeOverCurrent = ObtainDateUtils.isOverThirtyMinutesAfterTime(System.currentTimeMillis(), afterTime);
            if (isAfterTimeOverCurrent) {
                endTime = System.currentTimeMillis();
            } else {
                endTime = afterTime;
            }
        }
        SingleLineChartDTO singleLineChartDTO = new SingleLineChartDTO();
        singleLineChartDTO.setStartTime(startTime);
        singleLineChartDTO.setEndTime(endTime);
        singleLineChartDTO.setMeasureNum(measureNum);
        singleLineChartDTO.setSensorType(sensorType);

        List<LineChartDTO> lineChartDTOS = getLineChartData(startTime, endTime, mineId, measureNum);
        singleLineChartDTO.setLineChartDTOs(lineChartDTOS);

        return singleLineChartDTO;
    }

    /**
     * 根据时间范围标签设置查询条件
     */
    private void setTimeRangeCondition(LambdaEsQueryWrapper<MeasureActualEntity> queryWrapper,
                                       String range, Long startTime, Long endTime,
                                       long currentTimeMillis) {
        switch (range) {
            case ConstantsInfo.THIRTY_MINUTE_TAG:
                Long thirtyMinutesAgo = ObtainDateUtils.getThirtyMinutesTime(currentTimeMillis);
                queryWrapper.between(MeasureActualEntity::getDataTime, thirtyMinutesAgo, currentTimeMillis);
                break;
            case ConstantsInfo.ONE_HOUR_TAG:
                Long oneHourAgo = ObtainDateUtils.getOneHourTime(currentTimeMillis);
                queryWrapper.between(MeasureActualEntity::getDataTime, oneHourAgo, currentTimeMillis);
                break;
            case ConstantsInfo.TWENTY_FOUR_HOURS_TAG:
                Long twentyFourHoursAgo = ObtainDateUtils.getTwentyFourHoursTime(currentTimeMillis);
                queryWrapper.between(MeasureActualEntity::getDataTime, twentyFourHoursAgo, currentTimeMillis);
                break;
            case ConstantsInfo.ON_THAT_DAY_TAG:
                Long startOfDay = ObtainDateUtils.getCurrentZoneTime(currentTimeMillis);
                Long endOfDay = ObtainDateUtils.getCurrentTwentyFourHoursTime(currentTimeMillis);
                queryWrapper.between(MeasureActualEntity::getDataTime, startOfDay, endOfDay);
                break;
            case ConstantsInfo.CUSTOMIZE_TAG:
                if (ObjectUtil.isNull(startTime) || ObjectUtil.isNull(endTime)) {
                    throw new RuntimeException("开始时间/结束时间不能为空");
                }
                queryWrapper.between(MeasureActualEntity::getDataTime, startTime, endTime);
                break;
            default:
                throw new RuntimeException("不支持的时间范围类型: " + range);
        }
    }

    /**
     * 根据传感器类型设置LineGraphDTO的值字段
     */
    private void setLineGraphDTOValuesBySensorType(LineGraphDTO lineGraphDTO, MeasureActualEntity entity) {
        String sensorType = entity.getSensorType();

        // 顶部离层位移类型传感器需要设置浅基点值和深基点值
        if (ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE.equals(sensorType)) {
            lineGraphDTO.setValueShallow(entity.getValueShallow());
            lineGraphDTO.setValueDeep(entity.getValueDeep());
        }
        // 电磁辐射类型传感器需要设置电磁辐射强度极大值和电磁脉冲值
        else if (ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE.equals(sensorType)) {
            lineGraphDTO.setEleMaxValue(entity.getEleMaxValue());
            lineGraphDTO.setElePulse(entity.getElePulse());
        }
        // 其他类型传感器设置监测值
        else {
            lineGraphDTO.setMonitoringValue(entity.getMonitoringValue());
        }
    }

    /**
     * 根据不同类型获取测点编码列表
     */
    private List<String> getMeasureNums(ActualSelectDTO actualSelectDTO, Long mineId, String tag) {
        List<String> measureNums = new ArrayList<>();
        // 钻孔应力
        if (tag.equals("1")) {
            measureNums = drillingStressMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        // 巷道表面位移
        else if (tag.equals("2")) {
            measureNums = laneDisplacementMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        // 锚杆/索应力
        else if (tag.equals("3")) {
            measureNums = anchorCableStressMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        // 支架阻力
        else if (tag.equals("4")) {
            measureNums = supportResistanceMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        // 顶板离层位移
        else if (tag.equals("5")) {
            measureNums = roofAbscissionMapper.selectMeasureNumList(actualSelectDTO.getSurveyAreaName(), mineId);
        }
        return measureNums;
    }

    /**
     * 分页查询ES数据
     */
    private EsPageInfo<MeasureActualEntity> queryEsData(ActualSelectDTO actualSelectDTO, List<String> sensorTypes,
                                                        Long mineId, List<String> measureNums, Integer pageNum, Integer pageSize) {
        // 分页参数校验与默认值设置
        int validPageNum = validatePageUtils.validateAndSetDefaultPageNum(pageNum);
        int validPageSize = validatePageUtils.validateAndSetDefaultPageSize(pageSize);

        // 操作ES
        LambdaEsQueryWrapper<MeasureActualEntity> queryWrapper = new LambdaEsQueryWrapper<>();
        queryWrapper.in(MeasureActualEntity::getMeasureNum, measureNums)
                .in(MeasureActualEntity::getSensorType, sensorTypes)
                .eq(MeasureActualEntity::getMineId, mineId)
                .eq(StrUtil.isNotEmpty(actualSelectDTO.getMonitoringStatus()), MeasureActualEntity::getSensorStatus, actualSelectDTO.getMonitoringStatus())
                .between(ObjectUtil.isNotNull(actualSelectDTO.getStartTime()),
                        MeasureActualEntity::getDataTime,
                        actualSelectDTO.getStartTime(),
                        actualSelectDTO.getEndTime())
                .orderByDesc(MeasureActualEntity::getDataTime);

        return measureActualMapper.pageQuery(queryWrapper, validPageNum, validPageSize);
    }

    /**
     * 根据不同类型获取实体信息映射
     */
    private Map<String, Object> getEntityMap(EsPageInfo<MeasureActualEntity> pageInfo, Long mineId, String tag) {
        Map<String, Object> entityMap = new HashMap<>();

        if (pageInfo != null && pageInfo.getList() != null && !pageInfo.getList().isEmpty()) {
            // 提取所有测点编码并去重
            List<String> measureNumList = new ArrayList<>();
            for (MeasureActualEntity entity : pageInfo.getList()) {
                if (!measureNumList.contains(entity.getMeasureNum())) {
                    measureNumList.add(entity.getMeasureNum());
                }
            }

            // 根据不同类型查询相应的信息
            if (tag.equals("1")) {
                // 查询钻孔应力信息
                entityMap = getDrillingStressMap(measureNumList, mineId);
            } else if (tag.equals("2")) {
                // 查询巷道表面位移信息
                entityMap = getLaneDisplacementMap(measureNumList, mineId);
            } else if (tag.equals("3")) {
                // 查询锚杆/索应力信息
                entityMap = getAnchorCableStressMap(measureNumList, mineId);
            } else if (tag.equals("4")) {
                // 获取支架阻力信息
                entityMap = getSupportResistanceMap(measureNumList, mineId);
            } else if (tag.equals("5")) {
                // 获取顶板离层位移信息
                entityMap = getRoofAbscissionMap(measureNumList, mineId);
            }
        }

        return entityMap;
    }

    /**
     * 获取钻孔应力实体映射
     */
    private Map<String, Object> getDrillingStressMap(List<String> measureNumList, Long mineId) {
        Map<String, Object> entityMap = new HashMap<>();
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
            entityMap.put(entity.getMeasureNum(), entity);
        }
        return entityMap;
    }

    /**
     * 获取巷道表面位移实体映射
     */
    private Map<String, Object> getLaneDisplacementMap(List<String> measureNumList, Long mineId) {
        Map<String, Object> entityMap = new HashMap<>();
        List<LaneDisplacementEntity> laneDisplacementList = laneDisplacementMapper.selectList(
                new LambdaQueryWrapper<LaneDisplacementEntity>()
                        .in(LaneDisplacementEntity::getMeasureNum, measureNumList)
                        .eq(LaneDisplacementEntity::getMineId, mineId)
                        .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        .select(LaneDisplacementEntity::getMeasureNum,
                                LaneDisplacementEntity::getSurveyAreaName,
                                LaneDisplacementEntity::getSensorLocation)
        );

        // 构建以测点编码为键的Map，方便快速查找
        for (LaneDisplacementEntity entity : laneDisplacementList) {
            entityMap.put(entity.getMeasureNum(), entity);
        }
        return entityMap;
    }

    /**
     * 获取锚杆/索应力实体映射
     */
    private Map<String, Object> getAnchorCableStressMap(List<String> measureNumList, Long mineId) {
        Map<String, Object> entityMap = new HashMap<>();
        List<AnchorCableStressEntity> anchorCableStressList = anchorCableStressMapper.selectList(
                new LambdaQueryWrapper<AnchorCableStressEntity>()
                        .in(AnchorCableStressEntity::getMeasureNum, measureNumList)
                        .eq(AnchorCableStressEntity::getMineId, mineId)
                        .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        .select(AnchorCableStressEntity::getMeasureNum,
                                AnchorCableStressEntity::getSurveyAreaName,
                                AnchorCableStressEntity::getSensorLocation)
        );

        // 构建以测点编码为键的Map，方便快速查找
        for (AnchorCableStressEntity entity : anchorCableStressList) {
            entityMap.put(entity.getMeasureNum(), entity);
        }
        return entityMap;
    }

    /**
     * 获取支架阻力实体映射
     */
    private Map<String, Object> getSupportResistanceMap(List<String> measureNumList, Long mineId) {
        Map<String, Object> entityMap = new HashMap<>();
        List<SupportResistanceEntity> supportResistanceList = supportResistanceMapper.selectList(
                new LambdaQueryWrapper<SupportResistanceEntity>()
                        .in(SupportResistanceEntity::getMeasureNum, measureNumList)
                        .eq(SupportResistanceEntity::getMineId, mineId)
                        .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        .select(SupportResistanceEntity::getMeasureNum,
                                SupportResistanceEntity::getSurveyAreaName,
                                SupportResistanceEntity::getSensorLocation)
        );
        // 构建以测点编码为键的Map，方便快速查找
        for (SupportResistanceEntity entity : supportResistanceList) {
            entityMap.put(entity.getMeasureNum(), entity);
        }
        return entityMap;
    }

    /**
     * 获取 roofAbscission 实体映射
     */
    private Map<String, Object> getRoofAbscissionMap(List<String> measureNumList, Long mineId) {
        Map<String, Object> entityMap = new HashMap<>();
        List<RoofAbscissionEntity> roofAbscissionList = roofAbscissionMapper.selectList(
                new LambdaQueryWrapper<RoofAbscissionEntity>()
                        .in(RoofAbscissionEntity::getMeasureNum, measureNumList)
                        .eq(RoofAbscissionEntity::getMineId, mineId)
                        .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG)
                        .select(RoofAbscissionEntity::getMeasureNum,
                                RoofAbscissionEntity::getSurveyAreaName,
                                RoofAbscissionEntity::getSensorLocation)
        );
        // 构建以测点编码为键的Map，方便快速查找
        for (RoofAbscissionEntity entity : roofAbscissionList) {
            entityMap.put(entity.getMeasureNum(), entity);
        }
        return entityMap;
    }

    /**
     * 转换为DTO列表
     */
    private List<ActualDataDTO> convertToDTOList(EsPageInfo<MeasureActualEntity> pageInfo,
                                                 Map<String, Object> entityMap, String tag) {
        return ActualDataConverter.convertToDTOList(
                pageInfo != null ? pageInfo.getList() : null,
                new Function<String, String>() {
                    @Override
                    public String apply(String measureNum) {
                        // 根据测点编码获取监测区名称
                        Object entity = entityMap.get(measureNum);
                        if (entity != null) {
                            if (tag.equals("1") && entity instanceof DrillingStressEntity) {
                                return ((DrillingStressEntity) entity).getSurveyAreaName();
                            } else if (tag.equals("2") && entity instanceof LaneDisplacementEntity) {
                                return ((LaneDisplacementEntity) entity).getSurveyAreaName();
                            } else if (tag.equals("3") && entity instanceof AnchorCableStressEntity) {
                                return ((AnchorCableStressEntity) entity).getSurveyAreaName();
                            } else if (tag.equals("4") && entity instanceof SupportResistanceEntity) {
                                return ((SupportResistanceEntity) entity).getSurveyAreaName();
                            } else if (tag.equals("5") && entity instanceof RoofAbscissionEntity) {
                                return ((RoofAbscissionEntity) entity).getSurveyAreaName();
                            }
                        }
                        return null;
                    }
                },
                new Function<String, String>() {
                    @Override
                    public String apply(String measureNum) {
                        // 根据测点编码获取传感器位置作为传感器名称
                        Object entity = entityMap.get(measureNum);
                        if (entity != null) {
                            if (tag.equals("1") && entity instanceof DrillingStressEntity) {
                                return ((DrillingStressEntity) entity).getSensorLocation();
                            } else if (tag.equals("2") && entity instanceof LaneDisplacementEntity) {
                                return ((LaneDisplacementEntity) entity).getSensorLocation();
                            } else if (tag.equals("3") && entity instanceof AnchorCableStressEntity) {
                                return ((AnchorCableStressEntity) entity).getSensorLocation();
                            } else if (tag.equals("4") && entity instanceof SupportResistanceEntity) {
                                return ((SupportResistanceEntity) entity).getSensorLocation();
                            } else if (tag.equals("5") && entity instanceof RoofAbscissionEntity) {
                                return ((RoofAbscissionEntity) entity).getSensorLocation();
                            }
                        }
                        return null;
                    }
                });
    }



    private void checkBindParamNotNull(String measureNum, String range) {
        if (ObjectUtil.isNull(measureNum)) {
            throw new RuntimeException("测点编码不能为空");
        }
        if (ObjectUtil.isNull(range)) {
            throw new RuntimeException("时间范围不能为空");
        }
    }

    /**
     * 获取折线图数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param mineId 矿井ID
     * @param measureNum 测点编码
     * @return 折线图数据列表
     */
    private List<LineChartDTO> getLineChartData(Long startTime, Long endTime, Long mineId, String measureNum) {
        List<LineChartDTO> lineChartDTOs = new ArrayList<>();
        LambdaEsQueryWrapper<MeasureActualEntity> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.between(MeasureActualEntity::getDataTime, startTime, endTime)
                .eq(MeasureActualEntity::getMineId, mineId)
                .eq(MeasureActualEntity::getMeasureNum, measureNum);
        List<MeasureActualEntity> measureActualEntities = measureActualMapper.selectList(wrapper);
        if (!measureActualEntities.isEmpty()) {
            measureActualEntities.forEach(measureActualEntity -> {
                LineChartDTO lineChartDTO = new LineChartDTO();
                if (measureActualEntity.getSensorType().equals(ConstantsInfo.SUPPORT_RESISTANCE_TYPE) ||
                        measureActualEntity.getSensorType().equals(ConstantsInfo.DRILL_STRESS_TYPE) ||
                        measureActualEntity.getSensorType().equals(ConstantsInfo.ANCHOR_STRESS_TYPE) ||
                        measureActualEntity.getSensorType().equals(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE)
                        || measureActualEntity.getSensorType().equals(ConstantsInfo.LANE_DISPLACEMENT_TYPE)) {
                    lineChartDTO.setMonitoringValue(measureActualEntity.getMonitoringValue());
                } else if (measureActualEntity.getSensorType().equals(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE)) {
                    lineChartDTO.setMonitoringValue(measureActualEntity.getValueShallow());
                    lineChartDTO.setValueDeep(measureActualEntity.getValueDeep());
                } else if (measureActualEntity.getSensorType().equals(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE)) {
                    lineChartDTO.setEleMaxValue(measureActualEntity.getEleMaxValue());
                    lineChartDTO.setElePulse(measureActualEntity.getElePulse());
                }
                lineChartDTO.setDataTime(measureActualEntity.getDataTime());
                lineChartDTOs.add(lineChartDTO);
            });
        }
        return lineChartDTOs;
    }


}
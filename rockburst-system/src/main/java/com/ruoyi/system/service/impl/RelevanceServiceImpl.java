package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.*;
import com.ruoyi.system.domain.dto.RelevanceDTO;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.RelevanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: shikai
 * @date: 2025/9/3
 * @description:
 */

@Service
@Transactional
public class RelevanceServiceImpl extends ServiceImpl<RelevanceMapper, RelevanceEntity> implements RelevanceService {

    @Resource
    private RelevanceMapper relevanceMapper;

    @Resource
    private SupportResistanceMapper supportResistanceMapper;

    @Resource
    private DrillingStressMapper drillingStressMapper;

    @Resource
    private AnchorCableStressMapper anchorCableStressMapper;

    @Resource
    private RoofAbscissionMapper roofAbscissionMapper;

    @Resource
    private LaneDisplacementMapper laneDisplacementMapper;

    @Resource
    private ElecRadiationMapper elecRadiationMapper;


    @Override
    public int addRelevance(String sensorType, RelevanceDTO relevanceDTO, Long mineId) {
        int flag = 0;
        if (ObjectUtil.isNull(sensorType)) {
            throw new RuntimeException("传感器类型不能为空！");
        }
        if (ObjectUtil.isNull(relevanceDTO)) {
            throw new RuntimeException("关联信息不能为空！");
        }
        if (ObjectUtil.isNull(relevanceDTO.getMeasureNum()) || ObjectUtil.isNull(relevanceDTO.getWorkFaceId())) {
            throw new RuntimeException("测点编码、工作面id不能为空！");
        }
        RelevanceEntity relevanceEntity = new RelevanceEntity();
        relevanceEntity.setSensorType(sensorType);
        relevanceEntity.setMeasureNum(relevanceDTO.getMeasureNum());
        relevanceEntity.setOriginalWorkFaceName(relevanceDTO.getOriginalWorkFaceName());
        relevanceEntity.setWorkFaceId(relevanceDTO.getWorkFaceId());
        relevanceEntity.setMineId(mineId);
        flag = relevanceMapper.insert(relevanceEntity);
        if (flag > 0) {
            flag = updateWorkFaceIdBySensorType(sensorType, relevanceDTO.getMeasureNum(), relevanceDTO.getWorkFaceId());
        }
        return flag;
    }

    private int updateWorkFaceIdBySensorType(String sensorType, String measureNum, Long workFaceId) {
        // 使用Map存储传感器类型与对应Mapper的映射关系
        Map<String, Object> mapperMap = new HashMap<String, Object>() {{
            put(ConstantsInfo.SUPPORT_RESISTANCE_TYPE, supportResistanceMapper);
            put(ConstantsInfo.DRILL_STRESS_TYPE, drillingStressMapper);
            put(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE, anchorCableStressMapper);
            put(ConstantsInfo.ANCHOR_STRESS_TYPE, anchorCableStressMapper);
            put(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE, roofAbscissionMapper);
            put(ConstantsInfo.LANE_DISPLACEMENT_TYPE, laneDisplacementMapper);
            put(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE, elecRadiationMapper);
        }};

        // 使用Map存储传感器类型与对应实体类的映射关系
        Map<String, Class<?>> entityClassMap = new HashMap<String, Class<?>>() {{
            put(ConstantsInfo.SUPPORT_RESISTANCE_TYPE, SupportResistanceEntity.class);
            put(ConstantsInfo.DRILL_STRESS_TYPE, DrillingStressEntity.class);
            put(ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE, AnchorCableStressEntity.class);
            put(ConstantsInfo.ANCHOR_STRESS_TYPE, AnchorCableStressEntity.class);
            put(ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE, RoofAbscissionEntity.class);
            put(ConstantsInfo.LANE_DISPLACEMENT_TYPE, LaneDisplacementEntity.class);
            put(ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE, ElecRadiationEntity.class);
        }};

        if (!mapperMap.containsKey(sensorType)) {
            return 0;
        }

        Object mapper = mapperMap.get(sensorType);
        Class<?> entityClass = entityClassMap.get(sensorType);

        try {
            // 使用反射调用对应Mapper的selectOne方法
            Method selectOneMethod = mapper.getClass().getMethod("selectOne", Wrapper.class);

            // 构造查询条件
            LambdaQueryWrapper<?> queryWrapper = null;
            if (entityClass == SupportResistanceEntity.class) {
                queryWrapper = new LambdaQueryWrapper<SupportResistanceEntity>()
                        .eq(SupportResistanceEntity::getMeasureNum, measureNum)
                        .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
            } else if (entityClass == DrillingStressEntity.class) {
                queryWrapper = new LambdaQueryWrapper<DrillingStressEntity>()
                        .eq(DrillingStressEntity::getMeasureNum, measureNum)
                        .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
            } else if (entityClass == AnchorCableStressEntity.class) {
                queryWrapper = new LambdaQueryWrapper<AnchorCableStressEntity>()
                        .eq(AnchorCableStressEntity::getMeasureNum, measureNum)
                        .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
            } else if (entityClass == RoofAbscissionEntity.class) {
                queryWrapper = new LambdaQueryWrapper<RoofAbscissionEntity>()
                        .eq(RoofAbscissionEntity::getMeasureNum, measureNum)
                        .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
            } else if (entityClass == LaneDisplacementEntity.class) {
                queryWrapper = new LambdaQueryWrapper<LaneDisplacementEntity>()
                        .eq(LaneDisplacementEntity::getMeasureNum, measureNum)
                        .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
            } else if (entityClass == ElecRadiationEntity.class) {
                queryWrapper = new LambdaQueryWrapper<ElecRadiationEntity>()
                        .eq(ElecRadiationEntity::getMeasureNum, measureNum)
                        .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG);
            }

            Object entity = selectOneMethod.invoke(mapper, queryWrapper);
            if (ObjectUtil.isNotNull(entity)) {
                // 使用反射调用对应实体类的setWorkFaceId方法
                Method setWorkFaceIdMethod = entityClass.getMethod("setWorkFaceId", Long.class);
                setWorkFaceIdMethod.invoke(entity, workFaceId);

                // 使用反射调用对应Mapper的updateById方法
                Method updateByIdMethod = mapper.getClass().getMethod("updateById", entityClass);
                return (int) updateByIdMethod.invoke(mapper, entity);
            }
        } catch (Exception e) {
            log.error("更新工作面ID失败", e);
        }
        return 0;
    }

}
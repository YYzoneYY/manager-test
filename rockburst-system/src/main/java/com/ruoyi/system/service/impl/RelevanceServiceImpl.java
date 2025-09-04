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
        try {
            if (ConstantsInfo.SUPPORT_RESISTANCE_TYPE.equals(sensorType)) {
                SupportResistanceEntity entity = supportResistanceMapper.selectOne(new LambdaQueryWrapper<SupportResistanceEntity>()
                        .eq(SupportResistanceEntity::getMeasureNum, measureNum)
                        .eq(SupportResistanceEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(entity)) {
                    entity.setWorkFaceId(workFaceId);
                    return supportResistanceMapper.updateById(entity);
                }
            } else if (ConstantsInfo.DRILL_STRESS_TYPE.equals(sensorType)) {
                DrillingStressEntity entity = drillingStressMapper.selectOne(new LambdaQueryWrapper<DrillingStressEntity>()
                        .eq(DrillingStressEntity::getMeasureNum, measureNum)
                        .eq(DrillingStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(entity)) {
                    entity.setWorkFaceId(workFaceId);
                    return drillingStressMapper.updateById(entity);
                }
            } else if (ConstantsInfo.ANCHOR_CABLE_STRESS_TYPE.equals(sensorType) || ConstantsInfo.ANCHOR_STRESS_TYPE.equals(sensorType)) {
                AnchorCableStressEntity entity = anchorCableStressMapper.selectOne(new LambdaQueryWrapper<AnchorCableStressEntity>()
                        .eq(AnchorCableStressEntity::getMeasureNum, measureNum)
                        .eq(AnchorCableStressEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(entity)) {
                    entity.setWorkFaceId(workFaceId);
                    return anchorCableStressMapper.updateById(entity);
                }
            } else if (ConstantsInfo.ROOF_ABSCISSION_TYPE_TYPE.equals(sensorType)) {
                RoofAbscissionEntity entity = roofAbscissionMapper.selectOne(new LambdaQueryWrapper<RoofAbscissionEntity>()
                        .eq(RoofAbscissionEntity::getMeasureNum, measureNum)
                        .eq(RoofAbscissionEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(entity)) {
                    entity.setWorkFaceId(workFaceId);
                    return roofAbscissionMapper.updateById(entity);
                }
            } else if (ConstantsInfo.LANE_DISPLACEMENT_TYPE.equals(sensorType)) {
                LaneDisplacementEntity entity = laneDisplacementMapper.selectOne(new LambdaQueryWrapper<LaneDisplacementEntity>()
                        .eq(LaneDisplacementEntity::getMeasureNum, measureNum)
                        .eq(LaneDisplacementEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(entity)) {
                    entity.setWorkFaceId(workFaceId);
                    return laneDisplacementMapper.updateById(entity);
                }
            } else if (ConstantsInfo.ELECTROMAGNETIC_RADIATION_TYPE.equals(sensorType)) {
                ElecRadiationEntity entity = elecRadiationMapper.selectOne(new LambdaQueryWrapper<ElecRadiationEntity>()
                        .eq(ElecRadiationEntity::getMeasureNum, measureNum)
                        .eq(ElecRadiationEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                if (ObjectUtil.isNotNull(entity)) {
                    entity.setWorkFaceId(workFaceId);
                    return elecRadiationMapper.updateById(entity);
                }
            } else {
                throw new RuntimeException("不支持的传感器类型: " + sensorType);
            }

            throw new RuntimeException("未找到测点编码为 " + measureNum + " 的记录");
        } catch (Exception e) {
            throw new RuntimeException("更新工作面ID失败: " + e.getMessage());
        }
    }

}
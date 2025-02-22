package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.BizWorkface;
import com.ruoyi.system.domain.Entity.RelatesInfoEntity;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import com.ruoyi.system.domain.dto.AreaDTO;
import com.ruoyi.system.domain.dto.PointDTO;
import com.ruoyi.system.domain.dto.RelatesInfoDTO;
import com.ruoyi.system.mapper.BizWorkfaceMapper;
import com.ruoyi.system.mapper.RelatesInfoMapper;
import com.ruoyi.system.mapper.TunnelMapper;
import com.ruoyi.system.service.RelatesInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2024/12/11
 * @description:
 */

@Service
@Transactional
public class RelatesInfoServiceImpl extends ServiceImpl<RelatesInfoMapper, RelatesInfoEntity> implements RelatesInfoService {

    @Resource
    private RelatesInfoMapper relatesInfoMapper;

    @Resource
    private TunnelMapper tunnelMapper;

    @Resource
    private BizWorkfaceMapper bizWorkfaceMapper;

    @Override
    public void insert(Long planId, String planType, String type, List<RelatesInfoDTO> relatesInfoDTOS) {
//        ArrayList<RelatesInfoEntity> relatesInfoEntities = new ArrayList<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//        relatesInfoDTOS.forEach(relatesInfoDTO -> {
//            RelatesInfoEntity relatesInfoEntity = new RelatesInfoEntity();
//            relatesInfoEntity.setPlanId(planId);
//            relatesInfoEntity.setPlanType(planType);
//            relatesInfoEntity.setType(type);
//            relatesInfoEntity.setPositionId(relatesInfoDTO.getPositionId());
//            relatesInfoEntity.setDrillNumber(relatesInfoDTO.getDrillNumber());
//            relatesInfoEntity.setHoleDepth(relatesInfoDTO.getHoleDepth());
//            relatesInfoEntity.setSpacing(relatesInfoDTO.getSpacing());
//            try {
//                String area = objectMapper.writeValueAsString(relatesInfoDTO.getAreaDTOS());
//                relatesInfoEntity.setArea(area);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//            relatesInfoEntities.add(relatesInfoEntity);
//        });
//        this.saveBatch(relatesInfoEntities);
    }

    @Override
    public boolean deleteById(List<Long> planIdList) {
        return false;
//        boolean flag = false;
//        LambdaQueryWrapper<RelatesInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.in(RelatesInfoEntity::getPlanId, planIdList);
//        flag = this.remove(queryWrapper);
//        return flag;
    }


    @Override
    public List<RelatesInfoDTO> getByPlanId(Long planId) {
        return null;
//        List<RelatesInfoDTO> relatesInfoDTOS = new ArrayList<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<RelatesInfoEntity> relatesInfoEntities = relatesInfoMapper.selectList(new LambdaQueryWrapper<RelatesInfoEntity>()
//                .eq(RelatesInfoEntity::getPlanId, planId));
//        for (RelatesInfoEntity relatesInfoEntity : relatesInfoEntities) {
//            RelatesInfoDTO relatesInfoDTO = new RelatesInfoDTO();
//            relatesInfoDTO.setPositionId(relatesInfoEntity.getPositionId());
//            relatesInfoDTO.setDrillNumber(relatesInfoEntity.getDrillNumber());
//            relatesInfoDTO.setHoleDepth(relatesInfoEntity.getHoleDepth());
//            relatesInfoDTO.setSpacing(relatesInfoEntity.getSpacing());
//            // json转换
//            try {
//                List<AreaDTO> areaDTOS = objectMapper.readValue(relatesInfoEntity.getArea(),
//                        new TypeReference<List<AreaDTO>>() {});
//                relatesInfoDTO.setAreaDTOS(areaDTOS);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//            // 位置名称格式化
//            relatesInfoDTO.setPositionFmt(getPositionName(relatesInfoEntity.getPositionId(),
//                    relatesInfoEntity.getPlanType()));
//            relatesInfoDTOS.add(relatesInfoDTO);
//        }
//        return relatesInfoDTOS;
    }

    /**
     * 获取计划中已使用的导线点
     * @param planType 计划类型
     * @param type 类型
     * @param tunnelId 巷道id
     * @return 导线点集合
     */
    @Override
    public List<Long> getTraversePoint(String planType, String type, Long tunnelId) {
        return null;
//        List<Long> traversePointIdList = new ArrayList<>();
//        Set<Long> traversePointIdSet = new HashSet<>();
//        List<RelatesInfoEntity> relatesInfoEntities = relatesInfoMapper.selectList(new LambdaQueryWrapper<RelatesInfoEntity>()
//                .eq(RelatesInfoEntity::getType, type)
//                .eq(RelatesInfoEntity::getPlanType, planType)
//                .eq(RelatesInfoEntity::getPositionId, tunnelId));
//        if (ListUtils.isNotNull(relatesInfoEntities)) {
//            relatesInfoEntities.forEach(relatesInfoEntity -> {
//                if (ObjectUtil.isNotNull(relatesInfoEntity.getArea())) {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    try {
//                        List<AreaDTO> areaDTOS = objectMapper.readValue(relatesInfoEntity.getArea(),
//                                new TypeReference<List<AreaDTO>>() {});
//                        List<String> traversePoint = areaDTOS.stream().map(AreaDTO::getStartTraversePoint).collect(Collectors.toList());
//                        traversePoint.addAll(areaDTOS.stream().map(AreaDTO::getEndTraversePoint).collect(Collectors.toList()));
//                        traversePointIdSet.addAll(traversePoint.stream().map(Long::valueOf).collect(Collectors.toList()));
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        }
//        return  new ArrayList<>(traversePointIdSet);
    }

    /**
     * 获取位置
     */
    public String getPositionName(Long positionId, String type) {
        String positionName = "";
//        if (ObjectUtil.equals(type, ConstantsInfo.TUNNELING)) {
//            TunnelEntity tunnelEntity = tunnelMapper.selectOne(new LambdaQueryWrapper<TunnelEntity>()
//                    .eq(TunnelEntity::getTunnelId, positionId)
//                    .eq(TunnelEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
//            if (ObjectUtil.isNull(tunnelEntity)) {
//                return null;
//            }
//            positionName = tunnelEntity.getTunnelName();
//        } else if (ObjectUtil.equals(type, ConstantsInfo.STOPE)) {
//            BizWorkface bizWorkface = bizWorkfaceMapper.selectOne(new LambdaQueryWrapper<BizWorkface>()
//                    .eq(BizWorkface::getWorkfaceId, positionId)
//                    .eq(BizWorkface::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
//            if (ObjectUtil.isNull(bizWorkface)) {
//                return null;
//            }
//            positionName = bizWorkface.getWorkfaceName();
//        }
        return positionName;
    }
}
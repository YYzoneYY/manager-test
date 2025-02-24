package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.PlanAreaEntity;
import com.ruoyi.system.domain.dto.PlanAreaBatchDTO;
import com.ruoyi.system.domain.dto.PlanAreaDTO;
import com.ruoyi.system.domain.dto.TraversePointGatherDTO;
import com.ruoyi.system.mapper.PlanAreaMapper;
import com.ruoyi.system.service.PlanAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */

@Transactional
@Service
public class PlanAreaServiceImpl extends ServiceImpl<PlanAreaMapper, PlanAreaEntity> implements PlanAreaService {

    @Resource
    private PlanAreaMapper planAreaMapper;

    @Override
    public boolean insert(Long planId, String type, List<PlanAreaDTO> planAreaDTOS, List<TraversePointGatherDTO> traversePointGatherDTOS) {
        boolean flag = false;
        ArrayList<PlanAreaEntity> planAreaEntities = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        planAreaDTOS.forEach(planAreaDTO -> {
            PlanAreaEntity planAreaEntity = new PlanAreaEntity();
            planAreaEntity.setPlanId(planId);
            planAreaEntity.setType(type);
            planAreaEntity.setTunnelId(planAreaDTO.getTunnelId());
            planAreaEntity.setStartTraversePointId(planAreaDTO.getStartTraversePointId());
            planAreaEntity.setStartDistance(planAreaDTO.getStartDistance());
            planAreaEntity.setEndTraversePointId(planAreaDTO.getEndTraversePointId());
            planAreaEntity.setEndDistance(planAreaDTO.getEndDistance());
            try {
                String gather = objectMapper.writeValueAsString(traversePointGatherDTOS);
                planAreaEntity.setTraversePointGather(gather);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            planAreaEntities.add(planAreaEntity);
        });
        flag = this.saveBatch(planAreaEntities);
        return flag;
    }

    @Override
    public boolean batchInsert(List<PlanAreaBatchDTO> planAreaBatchDTOS) {
        boolean flag = false;
        List<PlanAreaEntity> planAreaEntities = new ArrayList<>();
        planAreaBatchDTOS.forEach(planAreaBatchDTO -> {
            PlanAreaEntity planAreaEntity = new PlanAreaEntity();
            planAreaEntity.setPlanId(planAreaBatchDTO.getPlanId());
            planAreaEntity.setType(planAreaBatchDTO.getType());
            planAreaEntity.setTunnelId(planAreaBatchDTO.getTunnelId());
            planAreaEntity.setStartTraversePointId(planAreaBatchDTO.getStartTraversePointId());
            planAreaEntity.setStartDistance(planAreaBatchDTO.getStartDistance());
            planAreaEntity.setEndTraversePointId(planAreaBatchDTO.getEndTraversePointId());
            planAreaEntity.setEndDistance(planAreaBatchDTO.getEndDistance());
            planAreaEntity.setTraversePointGather(planAreaBatchDTO.getTraversePointGather());
            planAreaEntities.add(planAreaEntity);
        });
        flag = this.saveBatch(planAreaEntities);
        return flag;
    }

    @Override
    public boolean deleteById(List<Long> planIdList) {
        boolean flag = false;
        flag = this.remove(new LambdaQueryWrapper<PlanAreaEntity>().in(PlanAreaEntity::getPlanId, planIdList));
        return flag;
    }

    @Override
    public List<PlanAreaDTO> getByPlanId(Long planId) {
        List<PlanAreaDTO> planAreaDTOS = new ArrayList<>();
        List<PlanAreaEntity> planAreaEntities = planAreaMapper.selectList(new LambdaQueryWrapper<PlanAreaEntity>()
                .eq(PlanAreaEntity::getPlanId, planId));
        if (ListUtils.isNotNull(planAreaEntities)) {
            planAreaEntities.forEach(planAreaEntity -> {
                PlanAreaDTO planAreaDTO = new PlanAreaDTO();
                planAreaDTO.setTunnelId(planAreaEntity.getTunnelId());
                planAreaDTO.setStartTraversePointId(planAreaEntity.getStartTraversePointId());
                planAreaDTO.setStartDistance(planAreaEntity.getStartDistance());
                planAreaDTO.setEndTraversePointId(planAreaEntity.getEndTraversePointId());
                planAreaDTO.setEndDistance(planAreaEntity.getEndDistance());
                planAreaDTOS.add(planAreaDTO);
            });
        }
        return planAreaDTOS;
    }
}
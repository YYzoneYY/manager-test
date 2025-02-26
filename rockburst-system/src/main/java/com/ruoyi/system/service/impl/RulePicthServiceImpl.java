package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ListUtils;
import com.ruoyi.system.domain.Entity.RulePitchEntity;
import com.ruoyi.system.domain.dto.RulePicthDTO;
import com.ruoyi.system.mapper.RulePitchMapper;
import com.ruoyi.system.service.RulePicthService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/2/26
 * @description:
 */

@Service
@Transactional
public class RulePicthServiceImpl extends ServiceImpl<RulePitchMapper, RulePitchEntity> implements RulePicthService {

    @Resource
    private RulePitchMapper rulePitchMapper;

    @Override
    public void insert(Long ruleConfigId, List<RulePicthDTO> rulePicthDTOS) {
        ArrayList<RulePitchEntity> rulePitchEntities = new ArrayList<>();
        rulePicthDTOS.forEach(rulePicthDTO -> {
            RulePitchEntity rulePitchEntity = new RulePitchEntity();
            BeanUtils.copyProperties(rulePicthDTO, rulePitchEntity);
            rulePitchEntity.setRuleConfigId(ruleConfigId);
            rulePitchEntities.add(rulePitchEntity);
        });
        this.saveBatch(rulePitchEntities);
    }

    @Override
    public boolean deleteByRuleConfigId(List<Long> ruleConfigIdList) {
        LambdaQueryWrapper<RulePitchEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RulePitchEntity::getRuleConfigId, ruleConfigIdList);
        return this.remove(queryWrapper);
    }

    @Override
    public List<RulePicthDTO> getList(Long ruleConfigId) {
        List<RulePicthDTO> rulePicthDTOS = new ArrayList<>();
        List<RulePitchEntity> rulePitchEntities = rulePitchMapper.selectList(new LambdaQueryWrapper<RulePitchEntity>()
                .eq(RulePitchEntity::getRuleConfigId, ruleConfigId));
        if (ListUtils.isNotNull(rulePitchEntities)) {
            rulePicthDTOS = rulePitchEntities.stream().map(rulePitchEntity -> {
                RulePicthDTO rulePicthDTO = new RulePicthDTO();
                BeanUtils.copyProperties(rulePitchEntity, rulePicthDTO);
                return rulePicthDTO;
            }).collect(Collectors.toList());
        }
        return rulePicthDTOS;
    }
}
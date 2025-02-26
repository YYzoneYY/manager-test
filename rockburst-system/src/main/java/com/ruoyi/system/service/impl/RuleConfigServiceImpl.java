package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.RuleConfigEntity;
import com.ruoyi.system.domain.dto.RuleConfigDTO;
import com.ruoyi.system.domain.dto.RulePicthDTO;
import com.ruoyi.system.domain.dto.project.AddRuleConfigDTO;
import com.ruoyi.system.mapper.RuleConfigMapper;
import com.ruoyi.system.service.RuleConfigService;
import com.ruoyi.system.service.RulePicthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */

@Service
@Transactional
public class RuleConfigServiceImpl extends ServiceImpl<RuleConfigMapper, RuleConfigEntity> implements RuleConfigService {

    @Resource
    private RuleConfigMapper ruleConfigMapper;

    @Resource
    private RulePicthService rulePicthService;


    /**
     * 添加规则配置
     * @param ruleConfigDTO 规则配置信息DTO
     * @return 返回结果
     */
    @Override
    public int insert(RuleConfigDTO ruleConfigDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(ruleConfigDTO)) {
            throw new RuntimeException("参数错误，参数不能为空!");
        }
        Long selectCount = ruleConfigMapper.selectCount(null);
        if (selectCount > 0) {
            throw new RuntimeException("规则配置只能有一个,新增失败");
        }
        RuleConfigEntity ruleConfigEntity = new RuleConfigEntity();
        BeanUtils.copyProperties(ruleConfigDTO, ruleConfigEntity);
        ruleConfigEntity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
        ruleConfigEntity.setCreateBy(SecurityUtils.getUserId());
        ruleConfigEntity.setCreateTime(System.currentTimeMillis());
        flag = ruleConfigMapper.insert(ruleConfigEntity);
        if (flag > 0) {
            if (ObjectUtil.isNotNull(ruleConfigDTO.getRulePicthDTOS()) && !ruleConfigDTO.getRulePicthDTOS().isEmpty()) {
                rulePicthService.insert(ruleConfigEntity.getRuleConfigId(), ruleConfigDTO.getRulePicthDTOS());
            }
        } else {
            throw new RuntimeException("规则配置添加失败");
        }
        return flag;
    }

    /**
     * 获取规则配置列表
     * @return 返回结果
     */
    @Override
    public int update(RuleConfigDTO ruleConfigDTO) {
        int flag = 0;
        if (ObjectUtil.isNull(ruleConfigDTO)) {
            throw new RuntimeException("参数错误，参数不能为空!");
        }
        if (ObjectUtil.isNull(ruleConfigDTO.getRuleConfigId())) {
            throw new RuntimeException("参数错误，id不能为空!");
        }
        RuleConfigEntity ruleConfigEntity = ruleConfigMapper.selectOne(new LambdaQueryWrapper<RuleConfigEntity>()
                .eq(RuleConfigEntity::getRuleConfigId, ruleConfigDTO.getRuleConfigId())
                .eq(RuleConfigEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
        if (ObjectUtil.isNull(ruleConfigEntity)) {
            throw new RuntimeException("未找到此规则配置,无法进行修改");
        }
        Long ruleConfigId = ruleConfigEntity.getRuleConfigId();
        BeanUtils.copyProperties(ruleConfigDTO, ruleConfigEntity);
        ruleConfigEntity.setRuleConfigId(ruleConfigId);
        ruleConfigEntity.setUpdateBy(SecurityUtils.getUserId());
        ruleConfigEntity.setUpdateTime(System.currentTimeMillis());
        flag = ruleConfigMapper.updateById(ruleConfigEntity);
        if (flag > 0) {
            List<Long> ruleConfigIdList = new ArrayList<>();
            ruleConfigIdList.add(ruleConfigId);
            rulePicthService.deleteByRuleConfigId(ruleConfigIdList);
            if (ObjectUtil.isNotNull(ruleConfigDTO.getRulePicthDTOS()) && !ruleConfigDTO.getRulePicthDTOS().isEmpty()) {
                rulePicthService.insert(ruleConfigId, ruleConfigDTO.getRulePicthDTOS());
            }
        } else {
            throw new RuntimeException("规则配置修改失败");
        }
        return flag;
    }

    /**
     * 获取规则配置列表
     * @return 返回结果
     */
    @Override
    public List<RuleConfigDTO> getRuleConfigList() {
        return ruleConfigMapper.selectList(new LambdaQueryWrapper<RuleConfigEntity>()
                .eq(RuleConfigEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG))
                .stream()
                .map(ruleConfigEntity -> {
                    RuleConfigDTO ruleConfigDTO = new RuleConfigDTO();
                    BeanUtils.copyProperties(ruleConfigEntity, ruleConfigDTO);
                    List<RulePicthDTO> rulePicthDTOS = rulePicthService.getList(ruleConfigEntity.getRuleConfigId());
                    ruleConfigDTO.setRulePicthDTOS(rulePicthDTOS);
                    return ruleConfigDTO;
                }).collect(Collectors.toList());
    }
}
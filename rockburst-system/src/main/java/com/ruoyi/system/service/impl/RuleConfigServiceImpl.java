package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.ConstantsInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.system.domain.Entity.RuleConfigEntity;
import com.ruoyi.system.domain.dto.RuleConfigDTO;
import com.ruoyi.system.domain.dto.project.AddRuleConfigDTO;
import com.ruoyi.system.mapper.RuleConfigMapper;
import com.ruoyi.system.service.RuleConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    /**
     * 批量插入规则配置
     * @param addRuleConfigDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public boolean insert(AddRuleConfigDTO addRuleConfigDTO) {
        boolean flag = false;
        if (ObjectUtil.isNull(addRuleConfigDTO)) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (addRuleConfigDTO.getRuleConfigDTOS() == null || addRuleConfigDTO.getRuleConfigDTOS().isEmpty()) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        long ts = System.currentTimeMillis();

        List<RuleConfigEntity> entities = addRuleConfigDTO.getRuleConfigDTOS().stream()
                .map(ruleConfigDTO -> {
                    RuleConfigEntity entity = new RuleConfigEntity();
                    entity.setRuleTag(ruleConfigDTO.getRuleTag());
                    entity.setRuleName(ruleConfigDTO.getRuleName());
                    entity.setRuleValue(ruleConfigDTO.getRuleValue());
                    entity.setCreateTime(ts);
                    entity.setCreateBy(SecurityUtils.getUserId());
                    entity.setUpdateTime(ts);
                    entity.setUpdateBy(SecurityUtils.getUserId());
                    entity.setDelFlag(ConstantsInfo.ZERO_DEL_FLAG);
                    return entity;
                }).collect(Collectors.toList());
        flag = this.saveBatch(entities);
        if (!flag) {
            throw new RuntimeException("新增失败");
        }
        return flag;
    }

    /**
     * 批量更新规则配置
     * @param addRuleConfigDTO 参数DTO
     * @return 返回结果
     */
    @Override
    public boolean update(AddRuleConfigDTO addRuleConfigDTO) {
        boolean flag = false;
        if (ObjectUtil.isNull(addRuleConfigDTO)) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (addRuleConfigDTO.getRuleConfigDTOS() == null || addRuleConfigDTO.getRuleConfigDTOS().isEmpty()) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        List<RuleConfigEntity> ruleConfigEntities = addRuleConfigDTO.getRuleConfigDTOS().stream()
                .map(ruleConfigDTO -> {
                    if (ObjectUtil.isNull(ruleConfigDTO.getRuleConfigId())) {
                        throw new RuntimeException("参数错误,规则配置id不能为空");
                    }
                    RuleConfigEntity entity = new RuleConfigEntity();
                    RuleConfigEntity ruleConfigEntity = ruleConfigMapper.selectOne(new LambdaQueryWrapper<RuleConfigEntity>()
                            .eq(RuleConfigEntity::getRuleConfigId, ruleConfigDTO.getRuleConfigId())
                            .eq(RuleConfigEntity::getDelFlag, ConstantsInfo.ZERO_DEL_FLAG));
                    if (ObjectUtil.isNull(ruleConfigEntity)) {
                        throw new RuntimeException("未找到此规则配置,无法进行修改");
                    }
                    Long ruleConfigId = ruleConfigEntity.getRuleConfigId();
                    BeanUtils.copyProperties(ruleConfigDTO, entity);
                    entity.setRuleConfigId(ruleConfigId);
                    entity.setUpdateTime(System.currentTimeMillis());
                    entity.setUpdateBy(SecurityUtils.getUserId());
                    return entity;
                }).collect(Collectors.toList());
        flag = this.updateBatchById(ruleConfigEntities);
        if (!flag) {
            throw new RuntimeException("修改失败");
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
                    return ruleConfigDTO;
                }).collect(Collectors.toList());
    }
}
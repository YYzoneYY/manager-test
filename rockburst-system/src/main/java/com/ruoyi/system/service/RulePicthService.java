package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.RulePitchEntity;
import com.ruoyi.system.domain.dto.RulePicthDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/26
 * @description:
 */
public interface RulePicthService extends IService<RulePitchEntity> {

    /**
     * 孔距规则保存
     * @param ruleConfigId 规则配置id
     * @param rulePicthDTOS 参数DTOs
     */
    void insert(Long ruleConfigId, List<RulePicthDTO> rulePicthDTOS);

    /**
     * 根据规则配置id删除
     * @param ruleConfigIdList 规则配置ids
     */
    boolean deleteByRuleConfigId(List<Long> ruleConfigIdList);

    List<RulePicthDTO> getList(Long ruleConfigId);
}
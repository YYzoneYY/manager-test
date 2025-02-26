package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.Entity.RuleConfigEntity;
import com.ruoyi.system.domain.dto.RuleConfigDTO;
import com.ruoyi.system.domain.dto.project.AddRuleConfigDTO;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */
public interface RuleConfigService extends IService<RuleConfigEntity> {


    /**
     * 添加规则配置
     * @param ruleConfigDTO 规则配置信息DTO
     * @return 返回结果
     */
    int insert(RuleConfigDTO ruleConfigDTO);


    int update(RuleConfigDTO ruleConfigDTO);

    /**
     * 获取规则配置列表
     * @return 返回结果
     */
    List<RuleConfigDTO> getRuleConfigList();
}
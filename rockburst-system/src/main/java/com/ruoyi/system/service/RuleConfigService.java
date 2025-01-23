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
     * 批量插入规则配置
     * @param addRuleConfigDTO 参数DTO
     * @return 返回结果
     */
    boolean insert(AddRuleConfigDTO addRuleConfigDTO);

    /**
     * 批量更新规则配置
     * @param addRuleConfigDTO 参数DTO
     * @return 返回结果
     */
    boolean update(AddRuleConfigDTO addRuleConfigDTO);

    /**
     * 获取规则配置列表
     * @return 返回结果
     */
    List<RuleConfigDTO> getRuleConfigList();
}
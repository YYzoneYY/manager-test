package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */

@Data
public class RuleConfigDTO {

    @ApiModelProperty("规则配置id")
    private Long ruleConfigId;

    @ApiModelProperty("规则标签")
    private String ruleTag;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("规则值")
    private String ruleValue;

}
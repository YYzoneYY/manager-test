package com.ruoyi.system.domain.dto.project;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ruoyi.system.domain.dto.RuleConfigDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */

@Data
public class AddRuleConfigDTO {

    private List<RuleConfigDTO> ruleConfigDTOS;
}
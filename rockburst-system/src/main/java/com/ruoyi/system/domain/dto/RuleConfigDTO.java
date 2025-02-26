package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.RuleConfigEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */

@Data
public class RuleConfigDTO extends RuleConfigEntity {

    @ApiModelProperty(value = "孔距")
    private List<RulePicthDTO> rulePicthDTOS;

}
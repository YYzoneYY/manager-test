package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class DistanceRuleDTO {

    @ApiModelProperty(value = "起始点距离")
    private String startPointDistance;

    @ApiModelProperty(value = "终始距离")
    private String endPointDistance;
}
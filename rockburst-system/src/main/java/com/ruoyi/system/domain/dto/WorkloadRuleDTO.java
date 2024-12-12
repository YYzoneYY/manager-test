package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class WorkloadRuleDTO {

    @ApiModelProperty(value = "时间占比")
    private String timeProportion;

    @ApiModelProperty(value = "工作量占比")
    private String workloadProportion;
}
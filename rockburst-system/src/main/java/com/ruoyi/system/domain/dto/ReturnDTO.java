package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/22
 * @description:
 */

@Data
public class ReturnDTO {

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty("区域信息")
    private List<PlanAreaDTO> planAreaDTOS;
}
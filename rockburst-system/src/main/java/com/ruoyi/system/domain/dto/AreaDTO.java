package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/11
 * @description:
 */

@Data
public class AreaDTO {

    private Long tunnelId;

    @ApiModelProperty(value = "起始导向点")
    private String startTraversePoint;

    @ApiModelProperty(value = "起始距离")
    private String startDistance;

    @ApiModelProperty(value = "终始导向点")
    private String endTraversePoint;

    @ApiModelProperty(value = "终始距离")
    private String endDistance;
}
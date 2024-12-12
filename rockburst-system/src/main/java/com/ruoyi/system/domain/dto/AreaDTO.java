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

    @ApiModelProperty(value = "导向点")
    private Long TraversePoint;

    @ApiModelProperty(value = "距离")
    private String distance;

    @ApiModelProperty(value = "标签(0:起始，1:终始)")
    private String tag;
}
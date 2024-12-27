package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/27
 * @description:
 */

@Data
public class ProjectWarnChoiceListDTO {

    @ApiModelProperty(value = "预警预警方案名称")
    private String label;

    @ApiModelProperty(value = "预警预警方案id")
    private Long value;
}
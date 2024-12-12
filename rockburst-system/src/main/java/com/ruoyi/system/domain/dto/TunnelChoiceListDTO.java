package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class TunnelChoiceListDTO {
    @ApiModelProperty("巷道名称")
    private String label;

    @ApiModelProperty("巷道Id")
    private Long value;
}
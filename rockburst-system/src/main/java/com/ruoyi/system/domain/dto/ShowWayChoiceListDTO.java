package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/2/25
 * @description:
 */

@Data
public class ShowWayChoiceListDTO {

    @ApiModelProperty("显示方式名称")
    private String label;

    @ApiModelProperty("显示方式标识")
    private String value;
}
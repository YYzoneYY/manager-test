package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/2/26
 * @description:
 */

@Data
public class RulePicthDTO {

    @ApiModelProperty(value = "危险区域id")
    private Long dangerAreaId;

    @ApiModelProperty(value = " 米数")
    private BigDecimal meterCount;
}
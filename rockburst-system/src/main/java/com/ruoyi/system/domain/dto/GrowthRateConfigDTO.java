package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/30
 * @description:
 */

@Data
public class GrowthRateConfigDTO {

    @ApiModelProperty("时长")
    private String duration;

    @ApiModelProperty("数值")
    private String numberValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("比较模式")
    private String compareType;

    @ApiModelProperty("等级")
    private String grade;
}
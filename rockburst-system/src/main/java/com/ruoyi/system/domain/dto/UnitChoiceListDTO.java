package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Data
public class UnitChoiceListDTO {

    @ApiModelProperty("施工单位名称")
    private String label;

    @ApiModelProperty("施工单位Id")
    private Long value;
}
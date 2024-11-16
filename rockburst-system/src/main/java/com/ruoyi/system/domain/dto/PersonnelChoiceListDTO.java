package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Data
public class PersonnelChoiceListDTO {

    @ApiModelProperty("施工人员名称")
    private String label;

    @ApiModelProperty("施工人员Id")
    private Long value;
}
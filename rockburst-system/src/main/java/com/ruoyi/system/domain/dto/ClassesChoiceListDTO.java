package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Data
public class ClassesChoiceListDTO {

    @ApiModelProperty("施工单位名称")
    private String label;

    @ApiModelProperty("施工单位Id")
    private Long value;
}
package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class FaceChoiceListDTO {

    @ApiModelProperty("工作面名称")
    private String label;

    @ApiModelProperty("工作面Id")
    private Long value;
}
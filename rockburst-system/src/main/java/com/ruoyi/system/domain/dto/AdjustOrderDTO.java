package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */

@Data
public class AdjustOrderDTO {

    @ApiModelProperty(value = "id")
    private Long dataId;

    @ApiModelProperty(value = "操作标识 1：上移 2：下移")
    private Integer operaType; //1上移,2下移
}
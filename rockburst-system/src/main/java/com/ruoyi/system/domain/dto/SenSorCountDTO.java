package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/26
 * @description:
 */

@Data
public class SenSorCountDTO {

    @ApiModelProperty("传感器类型")
    private String type;

    @ApiModelProperty("传感器数量")
    private Integer count;
}
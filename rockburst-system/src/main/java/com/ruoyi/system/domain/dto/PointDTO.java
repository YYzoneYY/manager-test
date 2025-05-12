package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/30
 * @description:
 */

@Data
public class PointDTO {

    @ApiModelProperty(value = "导线点id")
    private Long traversePointId;

    private String distance;
}
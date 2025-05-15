package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/2/18
 * @description:
 */

@Data
public class PlanAreaDTO {

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "起始导线点id")
    private Long startTraversePointId;

    @ApiModelProperty(value = "起始距离(+-)")
    private String startDistance;

    @ApiModelProperty(value = "终始导线点id")
    private Long endTraversePointId;

    @ApiModelProperty(value = "终始距离(+-)")
    private String endDistance;

    private Long workFaceId;

    private String startPointCoordinate;

    private String endPointCoordinate;
}
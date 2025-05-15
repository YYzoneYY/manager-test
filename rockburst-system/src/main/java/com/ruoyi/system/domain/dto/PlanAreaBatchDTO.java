package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/2/18
 * @description:
 */

@Data
public class PlanAreaBatchDTO {

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "工作面id")
    private Long  workFaceId;

    @ApiModelProperty(value = "类型(掘进or回采)")
    private String type;

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

    @ApiModelProperty(value = "导线点集合")
    private String traversePointGather;
}
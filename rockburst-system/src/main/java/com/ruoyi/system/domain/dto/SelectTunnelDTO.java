package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */

@Data
public class SelectTunnelDTO {

    @ApiModelProperty(value = "巷道名称")
    private String tunnelName;

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "巷道状态")
    private String tunnelStatus;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}
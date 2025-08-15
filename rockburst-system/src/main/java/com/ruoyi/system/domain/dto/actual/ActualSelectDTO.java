package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/14
 * @description:
 */

@Data
public class ActualSelectDTO {

    @ApiModelProperty(value = "监测区名称")
    private String surveyAreaName;

    @ApiModelProperty(value = "传感器状态")
    private String monitoringStatus;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;
}
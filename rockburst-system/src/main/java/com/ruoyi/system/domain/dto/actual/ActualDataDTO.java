package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/8/14
 * @description:
 */

@Data
public class ActualDataDTO {

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "传感器名称")
    private String sensorName;

    @ApiModelProperty(value = "传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "传感器位置")
    private String sensorLocation;

    @ApiModelProperty(value = "监测区名称")
    private String surveyAreaName;

    @ApiModelProperty(value = "监测值")
    private BigDecimal monitoringValue;

    @ApiModelProperty(value = "传感器状态")
    private String monitoringStatus;

    @ApiModelProperty(value = "数据时间")
    private Long dataTime;
}
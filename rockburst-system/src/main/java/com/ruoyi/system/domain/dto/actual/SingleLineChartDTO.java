package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/23
 * @description:
 */

@Data
public class SingleLineChartDTO {

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "曲线数据")
    private List<LineChartDTO> lineChartDTOs;
}
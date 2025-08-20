package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/8/20
 * @description:
 */

@Data
public class ParamAnalyzeDTO {

    @ApiModelProperty("传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "曲线图数据")
    private List<LineChartDTO> lineChartDTOs;
}
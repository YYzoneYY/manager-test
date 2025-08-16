package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */
@Data
public class LineGraphDTO {

    @ApiModelProperty(value = "传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "监测值")
    private BigDecimal monitoringValue;

    @ApiModelProperty(value = "浅基点值")
    private BigDecimal valueShallow;

    @ApiModelProperty(value = "深基点值")
    private BigDecimal valueDeep;

    @ApiModelProperty(value = "电磁辐射强度极大值")
    private BigDecimal eleMaxValue;

    @ApiModelProperty(value = "电磁脉冲")
    private BigDecimal elePulse;

    @ApiModelProperty("数据时间")
    private Long dataTime;
}
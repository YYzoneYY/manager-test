package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */

@Data
public class LineChartDTO {

    @ApiModelProperty(value = "监测值")
    private BigDecimal monitoringValue;

    @ApiModelProperty("数据时间")
    private Long dataTime;
}
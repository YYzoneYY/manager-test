package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/23
 * @description:
 */

@Data
public class SingleWarnSelectDTO {

    @ApiModelProperty(value = "时间范围(1-今日;2-本周;3-本月;4-自定义时间)")
    private String range;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;
}
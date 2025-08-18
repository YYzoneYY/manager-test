package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */

@Data
public class WarnSelectDTO {

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "预警类型")
    private String warnType;

    @ApiModelProperty(value = "预警等级")
    private String warnLevel;

    @ApiModelProperty(value = "预警状态")
    private String warnStatus;

    @ApiModelProperty(value = "处理状态")
    private String handStatus;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;
}
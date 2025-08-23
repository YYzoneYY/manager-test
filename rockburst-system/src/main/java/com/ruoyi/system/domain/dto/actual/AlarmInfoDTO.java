package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/23
 * @description:
 */

@Data
public class AlarmInfoDTO {

    @ApiModelProperty(value = "警情编号")
    private String warnInstanceNum;

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "监测值(处理后)")
    private String monitorValue;

    @ApiModelProperty(value = "警情内容")
    private String warnContent;

    @ApiModelProperty(value = "预警类型")
    private String warnType;

    @ApiModelProperty(value = "预警等级")
    private String warnLevel;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "开始时间格式化")
    private String startTimeFmt;

    @ApiModelProperty(value = "结束时间格式化")
    private String endTimeFmt;

    @ApiModelProperty(value = "预警类型格式化")
    private String warnTypeFmt;

    @ApiModelProperty(value = "预警等级格式化")
    private String warnLevelFmt;
}
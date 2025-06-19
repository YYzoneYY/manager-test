package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/16
 * @description:
 */

@Data
public class AlarmRecordDTO {

    @ApiModelProperty(value = "报警记录id")
    private Long alarmId;

    @ApiModelProperty(value = "报警类型")
    private String alarmType;

    @ApiModelProperty(value = "报警内容")
    private String alarmContent;

    @ApiModelProperty(value = "报警开始时间")
    private Long startTime;

    @ApiModelProperty(value = "报警结束时间")
    private Long endTime;

    @ApiModelProperty(value = "报警状态")
    private String alarmStatus;

    @ApiModelProperty(value = "报警类型格式化")
    private String alarmTypeFmt;

    @ApiModelProperty(value = "报警状态格式化")
    private String alarmStatusFmt;
}
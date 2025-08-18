package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/8/18
 * @description:
 */

@Data
public class WarnMessageVO {

    @ApiModelProperty(value = "预警信息id")
    private String warnMessageId;

    @ApiModelProperty(value = "警情编码")
    private String warnInstanceNum;

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "监测项")
    private String monitorItems;

    @ApiModelProperty(value = "监测值")
    private BigDecimal monitoringValue;

    @ApiModelProperty(value = "预警类型")
    private String warnType;

    @ApiModelProperty(value = "预警等级")
    private String warnLevel;

    @ApiModelProperty(value = "预警位置")
    private String warnLocation;

    @ApiModelProperty(value = "警情内容")
    private String warnContent;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "预警状态")
    private String warnStatus;

    @ApiModelProperty(value = "处理状态")
    private String handStatus;

    @ApiModelProperty(value = "开始时间格式化")
    private String startTimeFmt;

    @ApiModelProperty(value = "结束时间格式化")
    private String endTimeFmt;
}
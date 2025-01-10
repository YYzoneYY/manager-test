package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EqtSearchDto {

    @ApiModelProperty(value = "测区")
    private String surveyArea;

    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "开始时间str")
    private String startTimeStr;

    @ApiModelProperty(value = "结束时间str")
    private String endTimeStr;
}

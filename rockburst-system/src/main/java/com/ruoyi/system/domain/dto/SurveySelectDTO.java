package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/12
 * @description:
 */

@Data
public class SurveySelectDTO {

    @ApiModelProperty(value = "测区名称")
    private String surveyAreaName;

    @ApiModelProperty(value = "采区id")
    private Long miningAreaId;

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;
}
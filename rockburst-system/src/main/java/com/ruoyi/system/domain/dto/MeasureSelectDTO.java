package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Data
public class MeasureSelectDTO {

    @ApiModelProperty("监测区名称")
    private String surveyAreaName;

    @ApiModelProperty("所属工作面")
    private Long workFaceId;

    @ApiModelProperty("测点状态")
    private String status;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}
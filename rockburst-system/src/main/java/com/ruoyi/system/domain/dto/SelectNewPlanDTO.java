package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Data
public class SelectNewPlanDTO {

    @ApiModelProperty(value = "年度")
    private String annual;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;
}
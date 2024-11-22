package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@Data
public class SelectPlanDTO {

    @ApiModelProperty(value = "计划名称")
    private String planName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "施工单位")
    private Long constructionUnitId;

    @ApiModelProperty(value = "施工地点")
    private Long constructSite;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;
}
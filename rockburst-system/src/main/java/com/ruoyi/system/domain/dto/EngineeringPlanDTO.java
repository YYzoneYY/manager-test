package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.EngineeringPlanEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class EngineeringPlanDTO extends EngineeringPlanEntity {

    @ApiModelProperty(value = "施工单位名称")
    private String constructionUnitName;

    @ApiModelProperty(value = "施工地点名称")
    private String constructSiteFmt;

    @ApiModelProperty(value = "计划开始时间格式化")
    private String startTimeFmt;

    @ApiModelProperty(value = "计划结束时间格式化")
    private String endTimeFmt;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
}
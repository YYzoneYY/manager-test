package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.Entity.PlanEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/18
 * @description:
 */

@Data
public class PlanDTO extends PlanEntity {

    @ApiModelProperty(value = "区域信息")
    private List<PlanAreaDTO> planAreaDTOS;

    @ApiModelProperty(value = "计划开始时间格式化")
    private String startTimeFmt;


    @ApiModelProperty(value = "计划开始时间格式化")
    private String alarmCaptimeFmt;

    @ApiModelProperty(value = "计划结束时间格式化")
    private String endTimeFmt;

    @ApiModelProperty(value = "审核结果")
    private String auditResult;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
}
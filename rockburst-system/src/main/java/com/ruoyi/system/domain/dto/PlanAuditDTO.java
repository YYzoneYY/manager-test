package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Data
public class PlanAuditDTO {

    @ApiModelProperty(value = "审核id")
    private Long planAuditId;

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "审核结果")
    private String auditResult;

    @ApiModelProperty(value = "驳回原因")
    private String rejectionReason;

}
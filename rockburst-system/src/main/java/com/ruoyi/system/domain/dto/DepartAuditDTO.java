package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Data
public class DepartAuditDTO {

    @ApiModelProperty(value = "工程填报id")
    private Long projectId;

    @ApiModelProperty(value = "审核结果")
    private String auditResult;

    @ApiModelProperty(value = "驳回原因")
    private String rejectionReason;
}
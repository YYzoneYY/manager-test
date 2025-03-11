package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/3/11
 * @description:
 */

@Data
public class ReturnReasonDTO {

    @ApiModelProperty(value = "驳回标识")
    private String rejectTag;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
}
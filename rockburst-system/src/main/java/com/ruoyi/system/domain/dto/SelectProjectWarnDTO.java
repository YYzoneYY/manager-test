package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class SelectProjectWarnDTO {

    @ApiModelProperty(value = "方案名称")
    private String schemeName;

    @ApiModelProperty(value = "计划类型")
    private String planType;

    @ApiModelProperty(value = "状态(0:启用,1:禁用)")
    private String status;

}
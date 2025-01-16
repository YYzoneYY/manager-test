package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/16
 * @description:
 */

@Data
public class StaffDTO {

    @ApiModelProperty("人员名称")
    private String staffName;

    @ApiModelProperty("人员id")
    private Long staffId;
}
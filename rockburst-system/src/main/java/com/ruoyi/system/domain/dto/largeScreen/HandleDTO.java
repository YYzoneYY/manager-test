package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/19
 * @description:
 */

@Data
public class HandleDTO {

    @ApiModelProperty("报警id")
    private Long alarmId;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("处理备注")
    private String remarks;
}
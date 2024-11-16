package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/16
 * @description:
 */

@Data
public class ClassesSelectDTO {

    @ApiModelProperty("班次名称")
    private String classesName;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}
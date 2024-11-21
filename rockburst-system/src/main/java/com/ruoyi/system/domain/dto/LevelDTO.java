package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */

@Data
public class LevelDTO {

    @ApiModelProperty(value = "主键id")
    private Long dataId;

    @ApiModelProperty(value = "层级名称")
    private String levelName;

    @ApiModelProperty(value = "上一级id")
    private Long superId;
}
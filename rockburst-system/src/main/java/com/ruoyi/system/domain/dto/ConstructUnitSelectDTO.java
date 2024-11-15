package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/15
 * @description:
 */
@Data
public class ConstructUnitSelectDTO {

    @ApiModelProperty("施工单位名称")
    private String constructionUnitName;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}
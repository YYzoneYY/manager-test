package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: shikai
 * @date: 2024/11/25
 * @description:
 */

@Data
public class SelectProjectDTO {

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "施工地点")
    private Long constructSite;

    @ApiModelProperty(value = "施工单位")
    private Long constructionUnitId;

    @ApiModelProperty(value = "填报类型")
    private String fillingType;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
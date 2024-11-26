package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: shikai
 * @date: 2024/11/26
 * @description:
 */

@Data
public class SelectDeptAuditDTO {

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "施工地点")
    private Long constructSite;

    @ApiModelProperty(value = "施工单位")
    private Long constructionUnitId;

    @ApiModelProperty(value = "填报类型")
    private String fillingType;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: shikai
 * @date: 2024/11/25
 * @description:
 */

@Data
public class ProjectVO {

    @ApiModelProperty(value = "工程填报id")
    private Long projectId;

    @ApiModelProperty(value = "施工类型")
    private String constructType;

    @ApiModelProperty(value = "填报类型")
    private String drillType;

    @ApiModelProperty(value = "施工单位id")
    private Long constructUnitId;

    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    @ApiModelProperty(value = "施工地点id")
    private Long constructSiteId;

    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    @ApiModelProperty(value = "位置")
    private String location;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "施工单位名称")
    private String constructionUnitName;

    @ApiModelProperty(value = "施工地点名称")
    private String constructSiteFmt;

    @ApiModelProperty(value = "状态格式化")
    private String statusFmt;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
}
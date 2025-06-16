package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: shikai
 * @date: 2025/5/29
 * @description:
 */

@Data
public class ProjectDTO {

    @ApiModelProperty(value = "工程id")
    private Long projectId;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "施工单位")
    private Long constructUnitId;

    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    @ApiModelProperty(value = "施工地点")
    private String constructionSite;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "钻孔类型格式化")
    private String drillTypeFmt;

    @ApiModelProperty(value = "施工单位格式化")
    private String constructUnitFmt;

    @ApiModelProperty(value = "施工类型格式化")
    private String constructTypeFmt;

    private String constructType;
    private Long tunnelId;
    private Long workFaceId;
}
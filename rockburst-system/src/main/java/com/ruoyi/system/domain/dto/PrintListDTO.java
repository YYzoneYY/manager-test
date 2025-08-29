package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/8/28
 * @description:
 */
@Data
public class PrintListDTO {

    @ApiModelProperty(value = "工程id")
    private Long projectId;

    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "施工类型(回采/掘进)")
    private String constructType;

    @ApiModelProperty(value = "施工单位")
    private Long constructUnitId;

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "工作面id")
    private Long workfaceId;

    @ApiModelProperty(value = "施工时间")
    private String constructTime;

    @ApiModelProperty(value = "施工员id")
    private Long worker;

    @ApiModelProperty(value = "验收员id")
    private Long accepter;


    @ApiModelProperty(value = "钻孔孔径")
    private BigDecimal diameter;

    @ApiModelProperty(value = "计划孔深")
    private BigDecimal planDeep;

    @ApiModelProperty(value = "实际孔深")
    private BigDecimal realDeep;

    @ApiModelProperty(value = "动态现象")
    private String dynamicPhenomenon;


    @ApiModelProperty(value = "施工地点")
    private String constructionSite;

    @ApiModelProperty(value = "施工单位名称")
    private String constructUnitName;

    @ApiModelProperty(value = "施工员名称")
    private String workerName;

    @ApiModelProperty(value = "验收员名称")
    private String accepterName;

    @ApiModelProperty(value = "钻孔类型格式化")
    private String drillTypeFmt;

    @ApiModelProperty(value = "施工类型格式化")
    private String constructTypeFmt;
}
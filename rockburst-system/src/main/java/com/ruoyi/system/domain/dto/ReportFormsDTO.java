package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.system.domain.BizProjectRecord;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/2/26
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ReportFormsDTO extends BizProjectRecord {

    @ApiModelProperty(value = "钻孔高度")
    private BigDecimal height;

    @ApiModelProperty(name = "孔深")
    private BigDecimal realDeep;

    @ApiModelProperty(name = "钻孔直径")
    private BigDecimal diameter;

    @ApiModelProperty(name = "施工工具")
    private String borer;

    @ApiModelProperty(name = "施工时间格式化")
    private String constructTimeFmt;

    @ApiModelProperty(value = "钻孔类型格式化")
    private String drillHoleTypeFmt;

    @ApiModelProperty(value = "施工单位格式化")
    private String constructUnitFmt;

    @ApiModelProperty(value = "施工班次格式化")
    private String constructShiftFmt;

    @ApiModelProperty(value = "施工类型格式化")
    private String constructTypeFmt;

    @ApiModelProperty(value = "施工位置格式化")
    private String locationFmt;

    @ApiModelProperty(value = "施工人员格式化")
    private String workerFmt;

    @ApiModelProperty(value = "验收人员格式化")
    private String accepterFmt;
}
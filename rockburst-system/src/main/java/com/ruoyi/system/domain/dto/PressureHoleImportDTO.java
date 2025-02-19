package com.ruoyi.system.domain.dto;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/2/11
 * @description:
 */

@Data
public class PressureHoleImportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(name = "施工日期")
    @Excel(name = "施工日期", sort = 1, width = 25)
    private String constructTime;

    @ApiModelProperty(name = "钻孔类型")
    @Excel(name = "钻孔类型", sort = 2, width = 25)
    private String drillHoleType;

    @ApiModelProperty(name = "施工单位")
    @Excel(name = "施工单位", sort = 3, width = 25)
    private String constructUnit;

    @ApiModelProperty(name = "施工班次")
    @Excel(name = "施工班次", sort = 4, width = 25)
    private String constructShift;

    @ApiModelProperty(name = "施工类型")
    @Excel(name = "施工类型", sort = 5, width = 25)
    private String constructType;

    @ApiModelProperty(name = "施工位置")
    @Excel(name = "施工位置", sort = 6, width = 25)
    private String location;

    @ApiModelProperty(name = "钻孔编号")
    @Excel(name = "钻孔编号", sort = 7, width = 25)
    private String drillNum;

    @ApiModelProperty(name = "钻孔高度")
    @Excel(name = "钻孔高度", sort = 8, width = 25)
    private String height;

    @ApiModelProperty(name = "孔深")
    @Excel(name = "孔深", sort = 9, width = 25)
    private String realDeep;

    @ApiModelProperty(name = "钻孔直径")
    @Excel(name = "钻孔直径", sort = 10, width = 25)
    private String diameter;

    @ApiModelProperty(value = "施工人员")
    @Excel(name = "施工人员", sort = 11, width = 25)
    private String worker;

    @ApiModelProperty(value = "验收人员")
    @Excel(name = "验收人员", sort = 12, width = 25)
    private String accepter;

    @ApiModelProperty(name = "施工工具")
    @Excel(name = "施工工具", sort = 13, width = 25)
    private String borer;
}
package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class BizProjectRecordAddDto  {

    /** 工程id */
    @TableId( type = IdType.AUTO)
    private Long projectId;

    @ApiModelProperty(value = "钻孔类型",required = true)
    private String drillType;

    @ApiModelProperty(value = "计划Id")
    private Long planId;

    @ApiModelProperty(value = "施工类型 回踩 掘进",required = true)
    private String constructType;

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "施工时间",required = true)
    private Date constructTime;

    /** 施工单位 */
    @ApiModelProperty(value = "施工单位")
    private Long constructUnitId;

    /** 施工班次 */
    @ApiModelProperty(value = "施工班次")
    private Long constructShiftId;

    /** 位置 */
    @ApiModelProperty(value = "施工地点",required = true)
    private Long locationId;

    /** 距离 */
    @ApiModelProperty(value = "距离",required = true)
    private String constructRange;


    /** 定位方式 */
    @ApiModelProperty(value = "定位方式(一般都是导线点把)",required = true)
    private String positionType;

    /** 钢带起始 */
    @ApiModelProperty(value = "钢带起始")
    private String steelBeltStart;

    /** 导线点 */
    @ApiModelProperty(value = "导线点",required = true)
    private Long travePointId;

    /** 钢带终止 */
    @ApiModelProperty(value = "钢带终止")
    private String steelBeltEnd;

    /** 钻孔编号 */
    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    /** 状态 */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /** 施工负责人 */
    @ApiModelProperty(value = "施工负责人")
    private Long projecrHeader;

    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private Long worker;

    /** 安检员 */
    @ApiModelProperty(value = "爆破员")
    private Long bigbanger;

    /** 安检员 */
    @ApiModelProperty(value = "安检员")
    private Long securityer;

    /** 安检员 */
    @ApiModelProperty(value = "验收员")
    private Long accepter;

    /** 安检员 */
    @ApiModelProperty(value = "手机唯一标识")
    private String imem;


    List<BizDrillRecordDto> drillRecords;

    List<BizVideoDto>  videos;

}

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

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "计划Id")
    private Long planId;

    @ApiModelProperty(value = "施工类型 回踩 掘进")
    private String constructType;

    /** 施工时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "施工时间")
    private Date constructTime;

    /** 施工单位 */
    @ApiModelProperty(value = "施工单位")
    private Long constructUnitId;

    /** 施工班次 */
    @ApiModelProperty(value = "施工班次")
    private Long constructShiftId;

    /** 距离 */
    @ApiModelProperty(value = "距离")
    private String constructRange;

    /** 施工地点 */
    @ApiModelProperty(value = "施工地点= 巷道id")
    private Long tunnelId;

    /** 定位方式 */
    @ApiModelProperty(value = "定位方式(一般都是导线点把)")
    private String positionType;

    /** 钢带起始 */
    @ApiModelProperty(value = "钢带起始")
    private String steelBeltStart;

    /** 导线点 */
    @ApiModelProperty(value = "导线点")
    private Long travePointId;

    /** 钢带终止 */
    @ApiModelProperty(value = "钢带终止")
    private String steelBeltEnd;

    /** 钻孔编号 */
    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    /** 位置 */
    @ApiModelProperty(value = "位置")
    private String location;

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

    /** 验收视频源文件 */
    @ApiModelProperty(value = "验收视频源文件")
    private String originalFile;

    /** 进钻参数 */
    @ApiModelProperty(value = "进钻参数")
    private String drillParam;

    /** 钻屑量 */
    @ApiModelProperty(value = "钻屑量-有钻屑量的是必填")
    private String crumbWeight;

    /** 钻屑量 */
    @ApiModelProperty(value = "部门id")
    private Long deptId;


    @ApiModelProperty(value = "阅读状态")
    private Integer isRead;



    List<BizDrillRecordDto> drillRecords;

    List<BizVideoDto>  videos;

}

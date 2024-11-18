package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 工程填报记录对象 biz_project_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
@Accessors(chain = true)
public class BizProjectRecord extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** 工程id */
    @TableId( type = IdType.AUTO)
    private Long projectId;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "计划类型")
    private String planType;

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
    @ApiModelProperty(value = "施工地点")
    private String constructLocation;

    /** 定位方式 */
    @ApiModelProperty(value = "定位方式")
    private String positionType;

    /** 钢带起始 */
    @ApiModelProperty(value = "钢带起始")
    private String steelBeltStart;

    /** 导线点 */
    @ApiModelProperty(value = "导线点")
    private String travePointId;

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
    private String projecrHeader;

    /** 施工员 */
    @ApiModelProperty(value = "施工员")
    private String worker;

    /** 安检员 */
    @ApiModelProperty(value = "安检员")
    private String securityer;

    /** 验收视频源文件 */
    @ApiModelProperty(value = "验收视频源文件")
    private String originalFile;

    /** 进钻参数 */
    @ApiModelProperty(value = "进钻参数")
    private String drillParam;

    /** 钻屑量 */
    @ApiModelProperty(value = "钻屑量")
    private String crumbWeight;

    /** 钻屑量 */
    @ApiModelProperty(value = "钻屑量")
    private Long deptId;

    @ApiModelProperty(value = "钻屑量")
    private Integer isRead;
}

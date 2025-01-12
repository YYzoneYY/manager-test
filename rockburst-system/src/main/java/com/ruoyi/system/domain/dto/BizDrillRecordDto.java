package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 钻孔参数记录对象 biz_drill_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
@Accessors(chain = true)
public class BizDrillRecordDto
{
    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long drillRecordId;

    /** 关联填报id */
    @ApiModelProperty(name = "关联填报id")
    private Long projectId;

    /** 关联填报id */
    @ApiModelProperty(name = "计划id")
    private Long planId;

    /** 序号 */

    @ApiModelProperty(value = "序号")
    private Integer no;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /** 钻孔方向 */
    @ApiModelProperty(value = "钻孔方向")
    private String direction;

    /** 钻孔高度 */
    @ApiModelProperty(value = "钻孔高度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal height;

    /** 钻孔直径 */
    @ApiModelProperty(value = "钻孔直径")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal diameter;

    /** 计划深度 */
    @ApiModelProperty(value = "计划深度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal planDeep;

    /** 实际深度 */
    @ApiModelProperty(value = "实际深度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal realDeep;

    /** 实际深度 */
    @ApiModelProperty(value = "装药量")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal chargeWeight;

    /** 实际深度 */
    @ApiModelProperty(value = "封孔长度")
    @JsonSerialize(using= ToStringSerializer.class)
    private BigDecimal  pluggingLength;

    /** 实际深度 */
    @ApiModelProperty(value = "爆破时间")
    private Date  detonationTime;

    /** 实际深度 */
    @ApiModelProperty(value = "方位角")
    private String bearingAngle;


    /** 状态 */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    /** 状态 */
    @ApiModelProperty(value = "工具",required = true)
    private String borer;

//    @ApiModelProperty(name = "钻孔组")
//    private String drillCrumbJosn;

    @ApiModelProperty(value = "钻屑量",required = true)
    private String crumbWeight;

    @ApiModelProperty(value = "钻孔详情",required = true)
    private String detailJson;





}

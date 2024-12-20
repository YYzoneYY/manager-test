package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
    @ApiModelProperty(name = "序号",required = true)
    private Integer no;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "开始时间",required = true)
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "结束时间",required = true)
    private Date endTime;

    /** 钻孔方向 */
    @ApiModelProperty(name = "钻孔方向",required = true)
    private String direction;

    /** 钻孔高度 */
    @ApiModelProperty(name = "钻孔高度",required = true)
    private String height;

    /** 钻孔直径 */
    @ApiModelProperty(name = "钻孔直径",required = true)
    private String diameter;

    /** 计划深度 */
    @ApiModelProperty(name = "计划深度",required = true)
    private String planDeep;

    /** 实际深度 */
    @ApiModelProperty(name = "实际深度",required = true)
    private String realDeep;

    /** 实际深度 */
    @ApiModelProperty(name = "装药量")
    private String chargeWeight;

    /** 实际深度 */
    @ApiModelProperty(name = "封孔长度")
    private String  pluggingLength;

    /** 实际深度 */
    @ApiModelProperty(name = "爆破时间")
    private String  detonationTime;

    /** 实际深度 */
    @ApiModelProperty(name = "方位角")
    private String bearingAngle;


    /** 状态 */
    @ApiModelProperty(name = "状态")
    private Integer status;

    /** 状态 */
    @ApiModelProperty(name = "工具",required = true)
    private String borer;

    @ApiModelProperty(name = "钻孔组")
    private String drillCrumbJosn;

    @ApiModelProperty(name = "钻屑量",required = true)
    private String crumbWeight;

    @ApiModelProperty(name = "钻孔详情",required = true)
    private String detailJson;





}

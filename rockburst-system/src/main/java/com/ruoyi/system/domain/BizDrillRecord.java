package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 钻孔参数记录对象 biz_drill_record
 * 
 * @author ruoyi
 * @date 2024-11-09
 */
@Getter
@Setter
@Accessors(chain = true)
public class BizDrillRecord extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long drillRecordId;

    /** 关联填报id */
    @ApiModelProperty(name = "关联填报id")
    private Long projectId;

    /** 序号 */
    @ApiModelProperty(name = "序号")
    private Long no;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "结束时间")
    private Date endTime;

    /** 钻孔方向 */
    @ApiModelProperty(name = "钻孔方向")
    private String direction;

    /** 钻孔高度 */
    @ApiModelProperty(name = "钻孔高度")
    private String height;

    /** 钻孔直径 */
    @ApiModelProperty(name = "钻孔直径")
    private String diameter;

    /** 计划深度 */
    @ApiModelProperty(name = "计划深度")
    private String planDeep;

    /** 实际深度 */
    @ApiModelProperty(name = "实际深度")
    private String realDeep;

    /** 状态 */
    @ApiModelProperty(name = "状态")
    private Integer status;

    /** 状态 */
    @ApiModelProperty(name = "工具")
    private String borer;

    @ApiModelProperty(name = "钻孔组")
    private String drillCrumbJosn;

    @ApiModelProperty(name = "钻孔详情")
    private String detailJson;


}

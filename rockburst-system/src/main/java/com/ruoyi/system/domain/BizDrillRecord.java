package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class BizDrillRecord implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long drillRecordId;

    /** 关联填报id */
    @Excel(name = "关联填报id")
    private Long projectId;

    /** 序号 */
    @Excel(name = "序号")
    private Long no;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 钻孔方向 */
    @Excel(name = "钻孔方向")
    private String direction;

    /** 钻孔高度 */
    @Excel(name = "钻孔高度")
    private Long height;

    /** 钻孔直径 */
    @Excel(name = "钻孔直径")
    private String diameter;

    /** 计划深度 */
    @Excel(name = "计划深度")
    private String planDeep;

    /** 实际深度 */
    @Excel(name = "实际深度")
    private String realDeep;

    /** 状态 */
    @Excel(name = "状态")
    private String status;


    private String drillCrumbJosn;

    private String detailJson;


}

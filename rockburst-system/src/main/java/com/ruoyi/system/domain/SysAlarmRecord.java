package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseSelfEntity;
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
public class SysAlarmRecord extends BaseSelfEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId( type = IdType.AUTO)
    private Long recordId;

    /** 标题 */
    @ApiModelProperty(name = "标题")
    private String title;

    /** 简介 */
    @ApiModelProperty(name = "简介")
    private String briefly;

    /** 内容 */
    @ApiModelProperty(name = "内容")
    private String content;

    /** 状态 */
    @ApiModelProperty(name = "状态")
    private Integer status;

    /** 类型 */
    @ApiModelProperty(name = "类型")
    private String type;


    /** 用户id */
    @ApiModelProperty(name = "用户id")
    private Long userId;

    /** 用户id */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "推送时间")
    private Date pushTime;



}

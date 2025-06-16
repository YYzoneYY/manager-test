package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/6/10
 * @description:
 */

@Data
@ApiModel("报警记录表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("alarm_record")
public class AlarmRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("报警记录id")
    @TableId(value = "alarm_id", type = IdType.AUTO)
    private Long alarmId;

    @ApiModelProperty("报警类型")
    @TableField(value = "alarm_type")
    private String alarmType;

    @ApiModelProperty("计划id")
    @TableField(value = "plan_id")
    private Long planId;

    @ApiModelProperty("工程id")
    @TableField(value = "project_id")
    private Long projectId;

    @ApiModelProperty("报警值")
    @TableField(value = "quantity_alarm_value")
    private Integer quantityAlarmValue;

    @ApiModelProperty("报警阈值")
    @TableField(value = "quantity_alarm_threshold")
    private BigDecimal quantityAlarmThreshold;

    @ApiModelProperty("当前钻孔编号")
    @TableField(value = "current_drill_num")
    private String currentDrillNum;

    @ApiModelProperty("对比钻孔编号")
    @TableField(value = "contrast_drill_num")
    private String contrastDrillNum;

    @ApiModelProperty("实际距离")
    @TableField(value = "actual_distance")
    private Double actualDistance;

    @ApiModelProperty("安全间距")
    @TableField(value = "spaced")
    private Double spaced;

    @ApiModelProperty("报警内容")
    @TableField(value = "alarm_content")
    private String alarmContent;

    @ApiModelProperty("报警开始时间")
    @TableField(value = "start_time")
    private Long startTime;

    @ApiModelProperty("报警结束时间")
    @TableField(value = "end_time")
    private Long endTime;

    @ApiModelProperty("报警状态")
    @TableField(value = "alarm_status")
    private String alarmStatus;

    @ApiModelProperty("次数")
    @TableField(value = "num")
    private Integer num;

    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private Long createTime;
}
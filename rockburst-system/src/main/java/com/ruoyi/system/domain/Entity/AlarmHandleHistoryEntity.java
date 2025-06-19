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

/**
 * @author: shikai
 * @date: 2025/6/18
 * @description:
 */
@Data
@ApiModel("报警处理历史表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("alarm_handle_history")
public class AlarmHandleHistoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("报警处理历史id")
    @TableId(value = "alarm_handle_history_id", type = IdType.AUTO)
    private Long alarmHandleHistoryId;

    @ApiModelProperty("报警id")
    @TableField(value = "alarm_id")
    private Long alarmId;

    @ApiModelProperty("处理人")
    @TableField(value = "handle_person")
    private Long handlePerson;

    @ApiModelProperty("处理时间")
    @TableField(value = "handle_time")
    private Long handleTime;

    @ApiModelProperty("操作")
    @TableField(value = "operate")
    private String operate;

    @ApiModelProperty("备注")
    @TableField(value = "remarks")
    private String remarks;
}
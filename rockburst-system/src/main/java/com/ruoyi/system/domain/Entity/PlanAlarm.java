package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.core.domain.BaseToLongEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */

@Data
@ApiModel("计划预警")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PlanAlarm extends BaseToLongEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long alarmId;

    @ApiModelProperty(value = "计划id")
    @TableField()
    private Long planId;

    @ApiModelProperty(value = "状态")
    @TableField()
    private String status;

    @ApiModelProperty(value = "详细")
    @TableField()
    private String detail;

}
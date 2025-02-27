package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */

@Data
@ApiModel("规则孔距关联")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rule_pitch")
public class RulePitchEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "rule_pitch_id", type = IdType.AUTO)
    private Long rulePitchId;

    @ApiModelProperty(value = "规则id")
    @TableField(value = "rule_config_id")
    private Long ruleConfigId;

    @ApiModelProperty(value = "危险区id")
    @TableField(value = "danger_area_level")
    private String dangerAreaLevel;

    @ApiModelProperty(value = "米数")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "meter_count")
    private BigDecimal meterCount;

}
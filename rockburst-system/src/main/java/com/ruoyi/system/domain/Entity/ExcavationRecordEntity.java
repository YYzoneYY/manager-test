package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/13
 * @description:
 */
@Data
@ApiModel("掘进进尺记录表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("excavation_record")
public class ExcavationRecordEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("掘进进尺记录id")
    @TableId(value = "excavation_record_id", type = IdType.AUTO)
    private Long excavationRecordId;

    @ApiModelProperty("巷道id")
    @TableField("tunnel_id")
    private Long tunnelId;

    @ApiModelProperty("掘进进尺id")
    @TableField("excavation_footage_id")
    private Long excavationFootageId;

    @ApiModelProperty("掘进时间")
    @TableField("excavation_time")
    private Long excavationTime;

    @ApiModelProperty("掘进进度")
    @TableField("excavation_pace")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal excavationPace;

    @ApiModelProperty("修改掘进进度")
    @TableField("excavation_pace_edit")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal excavationPaceEdit;

    //0不用标识，1时间相同，2未填写3修改 4擦除
    @ApiModelProperty("修改标识")
    @TableField("flag")
    private String flag;
}
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
 * @date: 2025/2/24
 * @description:
 */

@Data
@ApiModel("回采进尺记录表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("mining_record_new")
public class MiningRecordNewEntity extends BusinessBaseEntity implements Serializable {

    @ApiModelProperty("回采进尺记录id")
    @TableId(value = "mining_record_id", type = IdType.AUTO)
    private Long miningRecordId;

    @ApiModelProperty("工作面id")
    @TableField("workface_id")
    private Long workFaceId;

    @ApiModelProperty("巷道id")
    @TableField("tunnel_id")
    private Long tunnelId;

    @ApiModelProperty("回采进尺id")
    @TableField("mining_footage_id")
    private Long miningFootageId;

    @ApiModelProperty("回采时间")
    @TableField("mining_time")
    private Long miningTime;

    @ApiModelProperty("回采进度")
    @TableField("mining_pace")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal miningPace;

    @ApiModelProperty("修改回采进度")
    @TableField("mining_pace_edit")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal miningPaceEdit;

    //0不用标识，1时间相同，2未填写3修改 4擦除
    @ApiModelProperty("修改标识")
    @TableField("flag")
    private String flag;
}
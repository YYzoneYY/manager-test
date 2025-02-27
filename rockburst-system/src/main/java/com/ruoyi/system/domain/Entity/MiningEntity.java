package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/2/24
 * @description:
 */

@Data
@ApiModel("回采进尺表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("mining_footage_new")
public class MiningEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("回采进尺id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "回采进尺id不能为空")
    @TableId(value = "mining_footage_id", type = IdType.AUTO)
    private Long miningFootageId;

    @ApiModelProperty("工作面id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "工作面id不能为空")
    @TableField("workface_id")
    private Long workFaceId;

    @ApiModelProperty("巷道id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "巷道id不能为空")
    @TableField("tunnel_id")
    private Long tunnelId;

    @ApiModelProperty("回采时间")
    @NotNull(groups = {ParameterValidationOther.class}, message = "回采时间不能为空")
    @TableField("mining_time")
    private Long miningTime;

    @ApiModelProperty("回采进度")
    @TableField("mining_pace")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal miningPace;

    //0正常数据 1未填写的 2修改 3 擦除
    @ApiModelProperty("修改标识 0:正常数据 1:未填写的 2:修改 3:擦除")
    @TableField("flag")
    private String flag;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}
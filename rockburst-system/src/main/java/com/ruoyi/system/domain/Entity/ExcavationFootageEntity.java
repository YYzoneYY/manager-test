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
 * @date: 2024/11/13
 * @description:
 */
@Data
@ApiModel("掘进进尺表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("excavation_footage")
public class ExcavationFootageEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("掘进进尺id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "掘进尺id不能为空")
    @TableId(value = "excavation_footage_id", type = IdType.AUTO)
    private Long excavationFootageId;

    @ApiModelProperty("巷道id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "巷道id不能为空")
    @TableField("tunnel_id")
    private Long tunnelId;

    @ApiModelProperty("掘进时间")
    @NotNull(groups = {ParameterValidationOther.class}, message = "掘进时间不能为空")
    @TableField("excavation_time")
    private Long excavationTime;

    @ApiModelProperty("掘进进度")
    @TableField("excavation_pace")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal excavationPace;

    //0正常数据 1未填写的 2修改 3 擦除
    @ApiModelProperty("修改标识")
    @TableField("flag")
    private String flag;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}
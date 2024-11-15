package com.ruoyi.system.constant.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.system.constant.GroupOther;
import com.ruoyi.system.constant.GroupUpdate;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/11
 * @description: 
 */

@Data
@ApiModel("测区表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("survey_area")
public class SurveyAreaEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "测区id")
    @NotNull(groups = {GroupUpdate.class}, message = "测区id不能为空")
    @TableId(value = "survey_area_id", type = IdType.AUTO)
    private Long surveyAreaId;

    @ApiModelProperty(value = "测区名称")
    @NotBlank(groups = {GroupOther.class}, message = "测区名称不能为空")
    @Size(max = 50, groups = {GroupOther.class}, message = "测区名称长度不能超过50")
    @TableField(value = "survey_area_name")
    private String surveyAreaName;

    @ApiModelProperty(value = "采区id")
    @NotNull(groups = {GroupOther.class}, message = "采区id不能为空")
    @TableField(value = "mining_area_id")
    private Long miningAreaId;

    @ApiModelProperty(value = "工作面id")
    @NotNull(groups = {GroupOther.class}, message = "工作面id不能为空")
    @TableField(value = "workface_id")
    private Long workFaceId;

    @ApiModelProperty(value = "巷道id")
    @NotNull(groups = {GroupOther.class}, message = "巷道id不能为空")
    @TableField(value = "tunnel_id")
    private Long tunnelId;

    @ApiModelProperty(value = "临界煤粉量")
    @TableField(value = "critical_braize")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal criticalBraize;

    @ApiModelProperty(value = "距左帮")
    @TableField(value = "left_gang")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal leftGang;

    @ApiModelProperty(value = "距右帮")
    @TableField(value = "right_gang")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal rightGang;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}
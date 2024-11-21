package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
 * @date: 2024/11/19
 * @description:
 */
@Data
@ApiModel("巷道表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tunnel")
public class TunnelEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "巷道id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "巷道id不能为空")
    @TableId(value = "tunnel_id", type = IdType.AUTO)
    private Long tunnelId;

    @ApiModelProperty(value = "巷道名称")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "巷道名称不能为空")
    @Size(max = 50, groups = {ParameterValidationOther.class}, message = "巷道名称长度不能超过50")
    @TableField("tunnel_name")
    private String tunnelName;

    @ApiModelProperty(value = "工作面id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "工作面id不能为空")
    @TableField("work_face_id")
    private Long workFaceId;

    @ApiModelProperty(value = "断面形状")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "断面形状不能为空")
    @TableField("section_shape")
    private String sectionShape;

    @ApiModelProperty(value = "支护形式")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "支护形式不能为空")
    @TableField("support_form")
    private String supportForm;

    @ApiModelProperty(value = "巷道宽度")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_width", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelWidth;

    @ApiModelProperty(value = "巷道高度")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_height", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelHeight;

    @ApiModelProperty(value = "巷道长度")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_length", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelLength;

    @ApiModelProperty(value = "巷道断面积")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "extent", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal extent;

    @ApiModelProperty(value = "巷道状态")
    @TableField("tunnel_status")
    private String tunnelStatus;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
}
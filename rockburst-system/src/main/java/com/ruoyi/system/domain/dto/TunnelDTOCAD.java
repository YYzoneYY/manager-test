package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.system.domain.Entity.ParameterValidationOther;
import com.ruoyi.system.domain.Entity.ParameterValidationUpdate;
import com.ruoyi.system.domain.Entity.TunnelEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/21
 * @description:
 */

@Data
public class TunnelDTOCAD  {


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
    @TableField("work_face_id")
    private Long workFaceId;

    @ApiModelProperty(value = "巷道类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "巷道类型不能为空")
    @TableField("tunnel_type")
    private String tunnelType;

    @ApiModelProperty(value = "断面形状")
    @TableField("section_shape")
    private String sectionShape;

    @ApiModelProperty(value = "支护形式")
    @TableField("support_form")
    private String supportForm;

    @ApiModelProperty(value = "巷道净宽")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_width", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelWidth;

    @ApiModelProperty(value = "巷道净高")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_height", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelHeight;

    @ApiModelProperty(value = "巷道长度(设计长度)")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "tunnel_length", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal tunnelLength;

    @ApiModelProperty(value = "巷道净断面积")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "extent", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal extent;

    @ApiModelProperty(value = "巷道状态")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "巷道状态不能为空")
    @TableField("tunnel_status")
    private String tunnelStatus;

    @ApiModelProperty("点")
    @TableField()
    private String pointsList;

    @ApiModelProperty(value = "中心点")
    @TableField()
    private String center;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    @ApiModelProperty("工作面名称")
    private String workFaceName;

    @ApiModelProperty("创建时间格式化")
    private String createTimeFrm;

    @ApiModelProperty("更新时间格式化")
    private String updateTimeFrm;

    @ApiModelProperty("断面形状格式化")
    private String sectionShapeFmt;

    @ApiModelProperty("支护形式格式化")
    private String supportFormFmt;

    @ApiModelProperty("中心点")
    private String workFaceCenter;

    @ApiModelProperty("迎头距离")
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal headDistance;


}
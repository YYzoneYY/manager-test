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
@ApiModel("工程计划")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("engineering_plan")
public class EngineeringPlanEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "计划id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "计划id不能为空")
    @TableId(value = "engineering_plan_id", type = IdType.AUTO)
    private Long engineeringPlanId;

    @ApiModelProperty(value = "计划名称")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "计划名称不能为空")
    @Size(max = 50, groups = {ParameterValidationOther.class}, message = "计划名称长度不能超过50")
    @TableField("plan_name")
    private String planName;

    @ApiModelProperty(value = "施工单位id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "建设单位id不能为空")
    @TableField("construction_unit_id")
    private Long constructionUnitId;

    @ApiModelProperty(value = "施工地点")
    @NotNull(groups = {ParameterValidationOther.class}, message = "工程部位id不能为空")
    @TableField("construct_site")
    private Long constructSite;

    @ApiModelProperty(value = "计划类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "计划类型不能为空")
    @TableField("plan_type")
    private String planType;

    @ApiModelProperty(value = "年月")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "计划日期不能为空")
    @TableField("date")
    private String date;

    @ApiModelProperty(value = "钻孔类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "钻孔类型不能为空")
    @TableField("drill_type")
    private String drillType;

    @ApiModelProperty(value = "钻孔个数")
    @NotNull(groups = {ParameterValidationOther.class}, message = "钻孔个数不能为空")
    private Integer drillNumber;

    @ApiModelProperty(value = "孔深")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "hole_depth", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal holeDepth;

    @ApiModelProperty(value = " 间距")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "spacing", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal spacing;

    @ApiModelProperty(value = " 布置方式")
    @NotBlank(groups = {ParameterValidationOther.class}, message = " 布置方式不能为空")
    @TableField("arrangement")
    private String arrangement;

    @ApiModelProperty(value = "计划开始时间")
    @NotNull(groups = {ParameterValidationOther.class}, message = "计划开始时间不能为空")
    @TableField("start_time")
    private Long startTime;

    @ApiModelProperty(value = "计划结束时间")
    @NotNull(groups = {ParameterValidationOther.class}, message = "计划结束时间不能为空")
    @TableField("end_time")
    private Long endTime;

    @ApiModelProperty(value = "类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "类型不能为空")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "状态")
    @TableField("state")
    private String state;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
}
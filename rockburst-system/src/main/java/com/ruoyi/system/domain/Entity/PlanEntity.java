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
@TableName("plan")
public class PlanEntity extends BusinessBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "计划id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "计划id不能为空")
    @TableId(value = "plan_id", type = IdType.AUTO)
    private Long planId;

    @ApiModelProperty(value = "年度")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "年度不能为空")
    @TableField("annual")
    private String annual;

    @ApiModelProperty(value = "归属月")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "月份不能为空")
    @TableField("monthly")
    private String monthly;

    @ApiModelProperty(value = "所属工作面")
    @NotNull(groups = {ParameterValidationOther.class}, message = "工作面id不能为空")
    @TableField("workface_id")
    private Long workFaceId;

    @ApiModelProperty(value = "计划类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "计划类型不能为空")
    @TableField("plan_type")
    private String planType;

    @ApiModelProperty(value = "类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "类型不能为空")
    @TableField("type")
    private String type;

    @ApiModelProperty(value = "计划名称")
    @Size(max = 50, groups = {ParameterValidationOther.class}, message = "计划名称长度不能超过50")
    @TableField("plan_name")
    private String planName;

    @ApiModelProperty(value = "总钻数")
    @TableField("total_drill_number")
    private Integer totalDrillNumber;

    @ApiModelProperty(value = "总孔深")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "total_hole_depth", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal totalHoleDepth;

    @ApiModelProperty(value = "钻孔类型")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "钻孔类型不能为空")
    @TableField("drill_type")
    private String drillType;

    @ApiModelProperty(value = "计划开始时间")
    @NotNull(groups = {ParameterValidationOther.class}, message = "计划开始时间不能为空")
    @TableField("start_time")
    private Long startTime;

    @ApiModelProperty(value = "计划结束时间")
    @NotNull(groups = {ParameterValidationOther.class}, message = "计划结束时间不能为空")
    @TableField("end_time")
    private Long endTime;

    @ApiModelProperty(value = "报警时间上限")
    @TableField()
    private Long alarmCaptime;

    @ApiModelProperty(value = "状态")
    @TableField("state")
    private String state;



    @ApiModelProperty(value = "工程预警方案id")
    @TableField("project_warn_scheme_id")
    private Long projectWarnSchemeId;

    @ApiModelProperty(value = "部门id")
    @TableField("dept_id")
    private Long deptId;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
}
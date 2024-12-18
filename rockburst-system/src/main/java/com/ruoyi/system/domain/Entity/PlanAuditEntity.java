package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/11/23
 * @description:
 */

@Data
@ApiModel("计划审核")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("plan_audit")
public class PlanAuditEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "审核id")
    @TableId(value = "plan_audit_id", type = IdType.AUTO)
    private Long planAuditId;

    @ApiModelProperty(value = "计划id")
    @NotNull(groups = {ParameterValidationOther.class}, message = "计划id不能为空")
    @TableField("plan_id")
    private Long planId;

    @ApiModelProperty(value = "审核结果")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "审核结果不能为空")
    @TableField("audit_result")
    private String auditResult;

    @ApiModelProperty(value = "驳回原因")
    @TableField("rejection_reason")
    private String rejectionReason;

    @ApiModelProperty(value = "次数")
    @TableField("number")
    private Integer number;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}
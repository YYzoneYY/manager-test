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
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/1/23
 * @description:
 */

@Data
@ApiModel("规则配置表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rule_config")
public class RuleConfigEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("规则配置id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "规则配置id不能为空")
    @TableId(value = "rule_config_id", type = IdType.AUTO)
    private Long ruleConfigId;

    @ApiModelProperty("规则标签")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "规则标签不能为空")
    @TableField("rule_tag")
    private String ruleTag;

    @ApiModelProperty("规则名称")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "规则名称不能为空")
    @TableField("rule_name")
    private String ruleName;

    @ApiModelProperty("规则值")
    @NotBlank(groups = {ParameterValidationOther.class}, message = "规则值不能为空")
    @TableField("rule_value")
    private String ruleValue;

    @ApiModelProperty(value = "删除标志(0存在2删除)")
    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}
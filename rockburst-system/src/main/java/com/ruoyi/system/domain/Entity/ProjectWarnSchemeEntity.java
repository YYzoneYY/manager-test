package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
@ApiModel("工程预警方案")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "project_warn_scheme")
public class ProjectWarnSchemeEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("工程预警方案id")
    @TableId(value = "project_warn_scheme_id", type = IdType.AUTO)
    private Long projectWarnSchemeId;

    @ApiModelProperty("方案名称")
    @TableField(value = "scheme_name")
    private String schemeName;

    @ApiModelProperty("计划类型")
    @TableField(value = "plan_type")
    private String planType;

    @ApiModelProperty("工作量规则")
    @TableField(value = "workload_rule")
    private String workloadRule;

    @ApiModelProperty("距离规则")
    @TableField(value = "distance_rule")
    private String distanceRule;

    @ApiModelProperty("状态(0:启用,1:禁用)")
    @TableField(value = "status")
    private String status;

    @ApiModelProperty("删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;
}
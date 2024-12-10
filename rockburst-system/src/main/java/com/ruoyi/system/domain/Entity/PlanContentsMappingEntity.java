package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2024/12/7
 * @description:
 */

@Data
@ApiModel("计划目录映射")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("plan_contents_mapping")
public class PlanContentsMappingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "计划id")
    @TableField("plan_id")
    private Long planId;

    @ApiModelProperty(value = "目录id")
    @TableField(value = "contents_id")
    private Long contentsId;


}
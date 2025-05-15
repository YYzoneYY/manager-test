package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.system.domain.BusinessBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/2/17
 * @description:
 */

@Data
@ApiModel("计划区域关联")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("plan_area")
public class PlanAreaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "plan_area_id", type = IdType.AUTO)
    private Long planAreaId;

    @ApiModelProperty(value = "计划id")
    @TableField(value = "plan_id")
    private Long planId;

    @ApiModelProperty(value = "类型(掘进or回采)")
    @TableField(value = "type")
    private String type;

    @ApiModelProperty(value = "巷道id")
    @TableField(value = "tunnel_id")
    private Long tunnelId;

    @ApiModelProperty(value = "起始导线点")
    @TableField(value = "start_traverse_point_id")
    private Long startTraversePointId;

    @ApiModelProperty(value = "起始距离")
    @TableField(value = "start_distance")
    private String startDistance;

    @ApiModelProperty(value = "起始导线点坐标")
    @TableField(value = "start_point_coordinate")
    private String startPointCoordinate;

    @ApiModelProperty(value = "终始导线点")
    @TableField(value = "end_traverse_point_id")
    private Long endTraversePointId;

    @ApiModelProperty(value = "终始距离")
    @TableField(value = "end_distance")
    private String endDistance;

    @ApiModelProperty(value = "终始导线点坐标")
    @TableField(value = "end_point_coordinate")
    private String endPointCoordinate;

    @ApiModelProperty(value = "导线点集合")
    @TableField(value = "traverse_point_gather")
    private String traversePointGather;
}
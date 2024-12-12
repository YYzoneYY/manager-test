package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/12/11
 * @description:
 */
@Data
@ApiModel("关联信息")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("relates_info")
public class RelatesInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联信息id")
    @TableId(value = "relates_info_id", type = IdType.AUTO)
    private Long relatesInfoId;

    @ApiModelProperty(value = "计划id")
    @TableField(value = "plan_id")
    private Long planId;

    @ApiModelProperty(value = "计划类型")
    @TableField(value = "plan_type")
    private String planType;

    @ApiModelProperty(value = "类型(1:掘进，2:回采)")
    @TableField(value = "type")
    private String type;

    @ApiModelProperty(value = "位置id")
    @TableField(value = "position_id")
    private Long positionId;

    @ApiModelProperty(value = "钻孔数量")
    @TableField(value = "drill_number")
    private Integer drillNumber;

    @ApiModelProperty(value = "总孔深")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "hole_depth", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal holeDepth;

    @ApiModelProperty(value = " spacing")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "spacing", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal spacing;

    @ApiModelProperty(value = "区域")
    @TableField(value = "area")
    private String area;
}
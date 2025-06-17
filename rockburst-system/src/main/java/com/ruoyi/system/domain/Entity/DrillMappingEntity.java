package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: shikai
 * @date: 2025/6/17
 * @description:
 */

@Data
@ApiModel("地质钻孔信息关联表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("drill_mapping")
public class DrillMappingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("地质钻孔信息关联id")
    @TableId(value = "drill_mapping_id", type = IdType.AUTO)
    private Long drillMappingId;

    @ApiModelProperty("地质钻孔id")
    @TableField(value = "geology_drill_id")
    private Long geologyDrillId;

    @ApiModelProperty("序号")
    @TableField(value = "num")
    private Integer num;

    @ApiModelProperty("标高")
    @TableField(value = "elevation")
    private String elevation;

    @ApiModelProperty("岩性")
    @TableField(value = "lithology")
    private String lithology;

    @ApiModelProperty("岩性编号")
    @TableField(value = "lithology_num")
    private String lithologyNum;

    @ApiModelProperty("厚度")
    @TableField(value = "thickness")
    private String thickness;

    @ApiModelProperty("埋深")
    @TableField(value = "buried_depth")
    private String buriedDepth;

    @ApiModelProperty("岩性描述")
    @TableField(value = "lithology_describe")
    private String lithologyDescribe;

    @ApiModelProperty("备注")
    @TableField(value = "remarks")
    private String remarks;
}
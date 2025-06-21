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
@ApiModel("地质钻孔表")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("geology_drill")
public class GeologyDrillEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("地质钻孔id")
    @TableId(value = "geology_drill_id", type = IdType.AUTO)
    private Long geologyDrillId;

    @ApiModelProperty("钻孔名称")
    @TableField(value = "data_name")
    private String dataName;

    @ApiModelProperty("标高")
    @TableField(value = "ground_elevation")
    private String groundElevation;

    @ApiModelProperty("底板标高")
    @TableField(value = "base_elevation")
    private String baseElevation;

    @ApiModelProperty("煤层厚度")
    @TableField(value = "coal_thickness")
    private String coalThickness;

    @ApiModelProperty("中心坐标")
    @TableField(value = "center")
    private String center;
}
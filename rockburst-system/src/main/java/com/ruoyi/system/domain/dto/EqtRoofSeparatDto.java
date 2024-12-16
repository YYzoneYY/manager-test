package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.common.core.domain.BaseSelfEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: 吴大林
 * @date: 2024/12/2
 * @description:
 */

@Getter
@Setter
@Accessors(chain = true)
public class EqtRoofSeparatDto extends BaseSelfEntity {

    @TableId( type = IdType.AUTO)
    private Long roofSeparatId;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("监测区域id")
    @TableField(value = "survey_area_id")
    private Long surveyAreaId;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    private Long workFaceId;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    private Long tunnelId;

    @ApiModelProperty("传感器类型")
    @TableField(value = "sensor_type")
    private String sensorType;

    @ApiModelProperty("传感器安装位置")
    @TableField(value = "sensor_location")
    private String sensorLocation;

    @ApiModelProperty("安装时间")
    @TableField(value = "install_time")
    private Long installTime;

    @ApiModelProperty("安装深度")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "install_depth_deep", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal installDepthDeep;

    @ApiModelProperty("安装深度")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "install_depth_shallow", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal installDepthShallow;


    @ApiModelProperty("初始应力")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "initial_stress", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal InitialStress;

    @ApiModelProperty("X轴")
    @TableField(value = "x_axis")
    private String xAxis;

    @ApiModelProperty("Y轴")
    @TableField(value = "y_axis")
    private String yAxis;

    @ApiModelProperty("Z轴")
    @TableField(value = "z_axis")
    private String zAxis;

    @ApiModelProperty("状态---是否启用（0是1否）")
    @TableField(value = "status")
    private String status;

    @ApiModelProperty("标识(1-手动新增，2-数采自动接入)")
    @TableField(value = "tag")
    private String tag;
}
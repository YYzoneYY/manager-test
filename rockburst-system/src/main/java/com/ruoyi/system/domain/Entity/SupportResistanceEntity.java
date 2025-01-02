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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/27
 * @description:
 */

@Data
@ApiModel("工作面支架阻力")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("support_resistance")
public class SupportResistanceEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("工作面支架阻力id")
    @TableId(value = "support_resistance_id", type = IdType.AUTO)
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "工作面支架阻力id不能为空")
    private Long supportResistanceId;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("监测区名称")
    @TableField(value = "survey_area_name")
    private Long surveyAreaName;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "工作面id不能为空")
    private Long workFaceId;

    @ApiModelProperty("传感器类型")
    @TableField(value = "sensor_type")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "传感器类型不能为空")
    private String sensorType;

    @ApiModelProperty("分站编号")
    @TableField(value = "substation_num")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "分站编号不能为空")
    private String substationNum;

    @ApiModelProperty("立柱架号不能为空")
    @TableField(value = "column_num")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "立柱架号不能为空")
    private String columnNum;

    @ApiModelProperty("立柱架名称")
    @TableField(value = "column_name")
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "立柱架名称不能为空")
    private String columnName;

    @ApiModelProperty("传感器安装位置")
    @TableField(value = "sensor_location")
    private String sensorLocation;

    @ApiModelProperty("泄压值")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "pressure_relief_value", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal pressureReliefValue;

    @ApiModelProperty("初撑力")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "setting_load", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal settingLoad;

    @ApiModelProperty("工作阻力")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField(value = "work_resistance", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal workResistance;

    @ApiModelProperty("X轴")
    @TableField(value = "x_axis")
    private String xAxis;

    @ApiModelProperty("Y轴")
    @TableField(value = "y_axis")
    private String yAxis;

    @ApiModelProperty("Z轴")
    @TableField(value = "z_axis")
    private String zAxis;

    @ApiModelProperty("数据时间")
    @TableField(value = "data_time")
    private Long dataTime;

    @ApiModelProperty("状态---是否启用（0是1否）")
    @TableField(value = "status")
    private String status;

    @ApiModelProperty("删除标志(0存在2删除)")
    @TableField("del_flag")
    @TableLogic(value = "0", delval = "2")
    private String delFlag;

    @ApiModelProperty("标识(1-手动新增，2-数采自动接入)")
    @TableField(value = "tag")
    private String tag;
}
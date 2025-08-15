package com.ruoyi.system.domain.Entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * @date: 2025/8/15
 * @description:
 */

@Data
@ApiModel("巷道位移")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("lane_displacement")
public class LaneDisplacementEntity extends BusinessBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("巷道表面位移id")
    @TableId(value = "displacement_id", type = IdType.AUTO)
    @NotNull(groups = {ParameterValidationUpdate.class}, message = "巷道表面位移id不能为空")
    private Long displacementId;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

    @ApiModelProperty("监测区名称")
    @TableField(value = "survey_area_name")
    private String surveyAreaName;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    private Long workFaceId;

    @ApiModelProperty("巷道id")
    @TableField(value = "tunnel_id")
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

    @ApiModelProperty("X轴")
    @TableField(value = "x_axis")
    @JsonProperty(value = "xAxis")
    private String xAxis;

    @ApiModelProperty("Y轴")
    @TableField(value = "y_axis")
    @JsonProperty(value = "yAxis")
    private String yAxis;

    @ApiModelProperty("Z轴")
    @TableField(value = "z_axis")
    @JsonProperty(value = "zAxis")
    private String zAxis;

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

    @ApiModelProperty("所属矿")
    @TableField(value = "mine_id")
    private Long mineId;

    @ApiModelProperty("所属公司")
    @TableField(value = "company_id")
    private Long companyId;
}
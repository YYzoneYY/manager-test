package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EqtEmeDto {
    @TableId( type = IdType.AUTO)
    private Long emeId;

    @ApiModelProperty("测点编码")
    @TableField(value = "measure_num")
    private String measureNum;

//    @ApiModelProperty("监测区域id")
//    @TableField(value = "survey_area_id")
//    private Long surveyAreaId;

    @ApiModelProperty("工作面id")
    @TableField(value = "workface_id")
    private Long workFaceId;

//    @ApiModelProperty("工作面id")
//    @TableField(value = "workface_id")
//    private Long tunnelId;

    @ApiModelProperty("传感器类型")
    @TableField(value = "scene_type")
    private String sceneType;

    @ApiModelProperty("传感器安装位置")
    @TableField(value = "sensor_location")
    private String sensorLocation;

    @ApiModelProperty("安装时间")
    @TableField(value = "install_time")
    private Long installTime;

//    @ApiModelProperty("安装深度")
//    @JsonSerialize(using = ToStringSerializer.class)
//    @TableField(value = "install_depth_deep", updateStrategy = FieldStrategy.IGNORED)
//    private BigDecimal installDepthDeep;
//
//    @ApiModelProperty("安装深度")
//    @JsonSerialize(using = ToStringSerializer.class)
//    @TableField(value = "install_depth_shallow", updateStrategy = FieldStrategy.IGNORED)
//    private BigDecimal installDepthShallow;


//    @ApiModelProperty("初始应力")
//    @JsonSerialize(using = ToStringSerializer.class)
//    @TableField(value = "initial_stress", updateStrategy = FieldStrategy.IGNORED)
//    private BigDecimal InitialStress;

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

    @ApiModelProperty(value = "预警方案id")
    @TableField(exist = false)
    private Long warnSchemeId;

    @ApiModelProperty("预警阈值配置")
    @TableField(exist = false)
    private List<Map<String, Object>> thresholdConfigDTOS;

    @ApiModelProperty("预警增量配置")
    @TableField(exist = false)
    private List<Map<String, Object>> incrementConfigDTOS;

    @ApiModelProperty("预警增速配置")
    @TableField(exist = false)
    private List<Map<String, Object>> growthRateConfigDTOS;
}

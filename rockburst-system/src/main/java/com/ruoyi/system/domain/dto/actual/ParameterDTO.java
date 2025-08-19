package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */

@Data
public class ParameterDTO {

    @ApiModelProperty(value = "数据id")
    private Long dataId;

    @ApiModelProperty(value = "传感器类型")
    private String sensorType;

    @ApiModelProperty(value = "测点编码")
    private String measureNum;

    @ApiModelProperty(value = "监测项")
    private String monitorItems;

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "传感器位置")
    private String sensorLocation;

    @ApiModelProperty(value = "工作面名称")
    private String workFaceName;
}
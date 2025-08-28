package com.ruoyi.system.domain.dto.actual;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/19
 * @description:
 */

@Data
public class MultipleParamPlanVO {

    @ApiModelProperty("多参量方案id")
    private Long multiplePlanId;

    @ApiModelProperty("参量名称")
    private String paramName;

    @ApiModelProperty("传感器位置")
    private String sensorLocation;

    @ApiModelProperty("警情编号")
    private String warnInstanceNum;

    @ApiModelProperty("测点编码")
    private String measureNum;

    @ApiModelProperty(value = "监测项")
    private String monitorItems;

    @ApiModelProperty("工作面id")
    private Long workFaceId;

    @ApiModelProperty("传感器类型")
    private String sensorType;

}
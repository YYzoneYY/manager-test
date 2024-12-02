package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.SupportResistanceEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Data
public class SupportResistanceVO extends SupportResistanceEntity {

    @ApiModelProperty(value = "工作面名称")
    private String workFaceName;

    @ApiModelProperty(value = "监测区域名称")
    private String surveyAreaName;

    @ApiModelProperty(value = "传感器编号")
    private String sensorNum;

    @ApiModelProperty(value = "数据时间格式化")
    private String dataTimeFmt;
}
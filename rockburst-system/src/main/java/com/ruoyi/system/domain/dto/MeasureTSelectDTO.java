package com.ruoyi.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/16
 * @description:
 */

@Data
public class MeasureTSelectDTO {

    @ApiModelProperty("传感器位置")
    private String sensorLocation;

    @ApiModelProperty("所属工作面")
    private Long workFaceId;

    @ApiModelProperty("测点状态")
    private String status;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;
}
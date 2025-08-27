package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/26
 * @description:
 */

@Data
public class DeviceCountDTO {

    @ApiModelProperty(value = "所有设备数")
    private Integer allDevice;

    @ApiModelProperty(value = "在线设备数")
    private Integer onlineDevice;

    @ApiModelProperty(value = "离线设备数")
    private Integer offlineDevice;
}
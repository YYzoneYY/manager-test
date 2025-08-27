package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/8/26
 * @description:
 */

@Data
public class QuantityDTO {

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "在线数量")
    private Integer onlineNumber;

    @ApiModelProperty(value = "离线数量")
    private Integer offlineNumber;
}
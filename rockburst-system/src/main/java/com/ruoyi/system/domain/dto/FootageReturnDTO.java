package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/5/27
 * @description:
 */

@Data
public class FootageReturnDTO {

    @ApiModelProperty("巷道id")
    private Long tunnelId;

    @ApiModelProperty("巷道生产邦结束坐标")
    private String tunnelScbEndCoordinate;

    @ApiModelProperty("当前累计进尺坐标")
    private String footageCoordinates;

    @ApiModelProperty("所处当前危险区id")
    private Long dangerAreaId;

    @ApiModelProperty("当前所处危险区剩余长度")
    private Double currentRemainingLength;

    @ApiModelProperty("距下一个危险区距离")
    private Double  nextDangerAreaDistance;
}
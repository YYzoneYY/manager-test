package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/14
 * @description:
 */
@Data
public class MiningSelectDTO {

    @ApiModelProperty(value = "工作面id")
    private Long workFaceId;

    @ApiModelProperty(value = "回采进度: 0指回采进度未填，1指的回采进度已填，2指的是全部")
    private String pace;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

}
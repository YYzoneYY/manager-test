package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/1/16
 * @description:
 */

@Data
public class UnitDataDTO {

    @ApiModelProperty("施工单位名称")
    private String key;

    @ApiModelProperty("施工单位Id")
    private Long value;

    @ApiModelProperty("工种")
    private List<ProfessionDTO> professionDTOS;
}
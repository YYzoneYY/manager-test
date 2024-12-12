package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/12/12
 * @description:
 */

@Data
public class MiningAreaChoiceListDTO {

    @ApiModelProperty("采区名称")
    private String label;

    @ApiModelProperty("采区Id")
    private Long value;

}
package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.parsers.SAXParser;

/**
 * @author: shikai
 * @date: 2025/8/27
 * @description:
 */

@Data
public class TagDropDownListDTO {

    @ApiModelProperty(value = "名称")
    private String label;

    @ApiModelProperty(value = "值")
    private String value;
}
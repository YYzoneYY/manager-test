package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2024/11/20
 * @description:
 */
@Data
public class SelectDocumentDTO {

    @ApiModelProperty(value = "主键id")
    private Long dataId;

    @ApiModelProperty(value = "文件名称")
    private String documentName;
}
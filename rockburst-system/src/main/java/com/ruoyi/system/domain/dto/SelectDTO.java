package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/1/13
 * @description:
 */

@Data
public class SelectDTO {

    @ApiModelProperty(value = "填报类型")
    private String fillingType;
    @ApiModelProperty(value = "施工单位")
    private Long constructionUnitId;

    public SelectDTO() {

    }

    public SelectDTO(String fillingType, Long constructionUnitId) {
        this.fillingType = fillingType;
        this.constructionUnitId = constructionUnitId;
    }
}
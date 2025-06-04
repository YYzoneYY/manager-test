package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/6/4
 * @description:
 */

@Data
public class ProjectTypeDTO {

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "数量")
    private Integer count;
}
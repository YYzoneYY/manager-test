package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/5/29
 * @description:
 */

@Data
public class ProjectDTO {

    @ApiModelProperty(value = "工程id")
    private Long projectId;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "施工单位")
    private Long constructUnitId;

    @ApiModelProperty(value = "施工时间")
    private Long  constructTime;

    @ApiModelProperty(value = "状态")
    private Integer status;
}
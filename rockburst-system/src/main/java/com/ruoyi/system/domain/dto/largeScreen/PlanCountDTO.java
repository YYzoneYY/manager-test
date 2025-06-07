package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/6/6
 * @description:
 */

@Data
public class PlanCountDTO {

    @ApiModelProperty(value = "归属月")
    private String monthly;

    @ApiModelProperty(value = "计划总数")
    private Integer planTotal;

    @ApiModelProperty(value = "已完成计划数")
    private Integer planCompleted;

    private List<Long> planIds;
}
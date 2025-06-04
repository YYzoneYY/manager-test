package com.ruoyi.system.domain.dto.largeScreen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: shikai
 * @date: 2025/6/4
 * @description:
 */

@Data
public class Select1DTO {

    @ApiModelProperty(value = "施工单位")
    private Long constructionUnitId;

    @ApiModelProperty(value = "填报类型")
    private String fillingType;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
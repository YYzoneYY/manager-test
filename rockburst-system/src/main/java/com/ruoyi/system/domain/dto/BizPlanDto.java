package com.ruoyi.system.domain.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BizPlanDto {
    private Long planId;
    @ApiModelProperty(value = "施工地点")
    private Long tunnelId;
    @ApiModelProperty(value = "填报类型")
    private String drillType;
    @ApiModelProperty(value = "查询时间")
    private String searchDate;
}

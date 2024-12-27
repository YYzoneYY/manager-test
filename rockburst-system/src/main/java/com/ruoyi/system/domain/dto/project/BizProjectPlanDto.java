package com.ruoyi.system.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "填报记录")
public class BizProjectPlanDto {

    @ApiModelProperty(value = "已过计划时间段 0 未过 1 不区分 2 (默认不区分)")
    private Integer isfinish;

//    @ApiModelProperty(value = "施工单位id")
//    private Long constructUnitId;

    @ApiModelProperty(value = "施工地点")
    private String locationId;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

//    @ApiModelProperty(value = "班次id")
//    private Long constructShiftId;

    @ApiModelProperty(value = "计划类型")
    private String type;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;



}

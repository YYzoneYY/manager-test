package com.ruoyi.system.domain.dto.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "防冲工程查询")
public class BizWashProofDto {


    @ApiModelProperty(value = "查询天数")
    private Integer dayNum;

    @ApiModelProperty(value = "施工地点")
    private String locationId;

    @ApiModelProperty(value = "施工地点名称")
    private String constructLocation;

    @ApiModelProperty(value = "钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "钻孔编号")
    private String drillNum;

    @ApiModelProperty(value = "班次id")
    private Long constructShiftId;

    @ApiModelProperty(value = "单位id")
    private Long constructUnitId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;



}

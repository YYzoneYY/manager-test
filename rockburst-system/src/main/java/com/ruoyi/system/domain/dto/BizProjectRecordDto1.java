package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "填报记录")
public class BizProjectRecordDto1 {








    @ApiModelProperty(value = "search -- 施工单位id")
    private Long constructUnitId;

    @ApiModelProperty(value = "search -- 施工地点")
    private Long tunnelId;

    @ApiModelProperty(value = "search -- 钻孔方向")
    private String direction;

    @ApiModelProperty(value = "search -- 钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "search -- 班次id")
    private String shiftId;

    @ApiModelProperty(value = "search -- 计划类型")
    private String planType;

    @ApiModelProperty(value = "search -- 状态")
    private Integer status;

    @ApiModelProperty(value = "search -- 开始时间")
    private String startTime;

    @ApiModelProperty(value = "search -- 结束时间")
    private String endTime;

    @ApiModelProperty(value = "search -- 某天")
    private String oneTime;

}

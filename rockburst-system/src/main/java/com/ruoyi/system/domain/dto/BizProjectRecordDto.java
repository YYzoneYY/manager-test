package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "填报记录")
public class BizProjectRecordDto {

    @Schema(description = "审核 主键")
    private Long projectId;

    @ApiModelProperty(value = "审核 填报0 通过 1 驳回 2")
    @Schema(description = "审核 填报0 通过 1 驳回 2")
    private Integer audit;

    @ApiModelProperty(value = "审核 退回原因")
    private String msg;

    @ApiModelProperty(value = "search -- 查询天数")
    private Integer dayNum;


    @ApiModelProperty(value = "search -- 施工单位id")
    private Long constructUnitId;

    @ApiModelProperty(value = "search -- 施工地点id")
    private Long constructLocationId;

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

}

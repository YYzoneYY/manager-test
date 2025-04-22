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

    @ApiModelProperty(value = "科室 deart 区队 team")
    @Schema(description = "科室 deart 区队 team")
    private String audit;
//
    @ApiModelProperty(value = "工作面Id")
    private Long workfaceId;

    @ApiModelProperty(value = "巷道id")
    private Long tunnelId;

    @ApiModelProperty(value = "search -- 查询天数")
    private Integer dayNum;


    @ApiModelProperty(value = "search -- 施工单位id")
    private Long constructUnitId;

    @ApiModelProperty(value = "search -- 施工地点")
    private String locationId;

    @ApiModelProperty(value = "search -- 钻孔类型")
    private String drillType;

    @ApiModelProperty(value = "search -- 班次id")
    private Long constructShiftId;

    @ApiModelProperty(value = "掘进回采")
    private String constructType;

    @ApiModelProperty(value = "search -- 状态")
    private Integer status;

    @ApiModelProperty(value = "search -- 开始时间")
    private String startTime;

    @ApiModelProperty(value = "search -- 结束时间")
    private String endTime;



}

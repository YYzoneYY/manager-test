package com.ruoyi.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BizProjectRecordDto {

    @Schema(description = "审核 主键")
    private Long projectId;

    @Schema(description = "审核 填报0 通过 1 驳回 2")
    private Integer audit;

    @Schema(description = "审核 退回原因")
    private String msg;

    @Schema(description = "search -- 查询天数")
    private Integer dayNum;


    @Schema(description = "search -- 施工单位id")
    private Long constructUnitId;

    @Schema(description = "search -- 施工地点id")
    private Long constructLocationId;

    @Schema(description = "search -- 钻孔类型")
    private String drillType;

    @Schema(description = "search -- 班次id")
    private String shiftId;

    @Schema(description = "search -- 计划类型")
    private String planType;

    @Schema(description = "search -- 状态")
    private Integer status;

    @Schema(description = "search -- 开始时间")
    private String startTime;

    @Schema(description = "search -- 结束时间")
    private String endTime;

}

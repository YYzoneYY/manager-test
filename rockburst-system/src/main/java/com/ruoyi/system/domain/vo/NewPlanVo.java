package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.PlanAreaDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/19
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class NewPlanVo extends PlanEntity {

    @ApiModelProperty(value = "区域信息")
    private List<PlanAreaDTO> planAreaDTOS;

    @ApiModelProperty(value = "计划开始时间格式化")
    private String startTimeFmt;

    @ApiModelProperty(value = "计划结束时间格式化")
    private String endTimeFmt;

    @ApiModelProperty(value = "所属工作面")
    private String workFaceName;

    @ApiModelProperty(value = "状态格式化")
    private String statusFmt;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
}
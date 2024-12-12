package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Entity.PlanEntity;
import com.ruoyi.system.domain.dto.RelatesInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author: shikai
 * @date: 2024/11/22
 * @description:
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class PlanVO extends PlanEntity {

    @ApiModelProperty(value = "目录id")
    private Long contentsId;

    @ApiModelProperty(value = "关联信息")
    private List<RelatesInfoDTO> relatesInfoDTOS;

    @ApiModelProperty(value = "计划开始时间格式化")
    private String startTimeFmt;

    @ApiModelProperty(value = "计划结束时间格式化")
    private String endTimeFmt;

    @ApiModelProperty(value = "状态格式化")
    private String statusFmt;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;
}
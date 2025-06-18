package com.ruoyi.system.domain.dto.largeScreen;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/6/13
 * @description:
 */

@Data
public class PlanPushDTO {

    // 预警类型
    private String alarmType;
    // 预警时间
    private Long alarmTime;
    // 计划id
    private Long planId;
    // 计划开始时间
    private Long planStartTime;
    // 计划结束时间
    private Long planEndTime;
    // 工作面id
    private Long workFaceId;
    // 工作面名称
    private String workFaceName;
    // 计划总数量(钻孔)
    private Integer planQuantity;
    // 实际完成数量(钻孔)
    private Integer actualCompleteQuantity;
}
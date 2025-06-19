package com.ruoyi.system.domain.dto.largeScreen;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/6/13
 * @description:
 */

@Data
public class PlanPushDTO implements AlarmMessage{

    // 预警id
    private Long alarmId;
    // 预警类型
    private String alarmType;
    // 预警时间
    private Long alarmTime;
    // 告警内容
    private String alarmContent;
    // 计划id
    private Long planId;
    // 工作面id
    private Long workFaceId;
    // 工作面名称
    private String workFaceName;
    // 计划总数量(钻孔)
    private Integer planQuantity;
    // 实际完成数量(钻孔)
    private Integer actualCompleteQuantity;
}
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

    private String alarmType;

    private Long alarmTime;

    private Long planId;

    private Long planStartTime;

    private Long planEndTime;

    private Long workFaceId;

    private String workFaceName;

    private Integer planQuantity;

    private Integer actualCompleteQuantity;
}
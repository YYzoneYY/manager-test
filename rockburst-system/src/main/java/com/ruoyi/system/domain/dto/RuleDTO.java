package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2025/6/18
 * @description:
 */

@Data
public class RuleDTO {
    private BigDecimal timeProportion;
    private BigDecimal workloadProportion;
}
package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/29
 * @description:
 */

@Data
public class ThresholdConfigDTO {

    private BigDecimal numberValue;

    private String unit;

    private String compareType;

    private String grade;
}
package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: shikai
 * @date: 2024/11/30
 * @description:
 */

@Data
public class GrowthRateConfigDTO {

    private String duration;

    private String numberValue;

    private String unit;

    private String compareType;

    private String grade;
}
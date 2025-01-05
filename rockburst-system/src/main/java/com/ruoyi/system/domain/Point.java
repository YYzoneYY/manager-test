package com.ruoyi.system.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Point {
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal z;
}

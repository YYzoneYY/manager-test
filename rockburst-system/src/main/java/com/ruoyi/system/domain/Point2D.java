package com.ruoyi.system.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Point2D {
    private BigDecimal x;
    private BigDecimal y;
    private Long areaId;
    private String barType;

    public Point2D() {
    }

    public Point2D(BigDecimal x, BigDecimal y) {
        this.x = x;
        this.y = y;
    }

    public Point2D(BigDecimal x, BigDecimal y, Long areaId) {
        this.x = x;
        this.y = y;
        this.areaId = areaId;
    }

    public Point2D(BigDecimal x, BigDecimal y, Long areaId, String barType) {
        this.x = x;
        this.y = y;
        this.areaId = areaId;
        this.barType = barType;
    }
}

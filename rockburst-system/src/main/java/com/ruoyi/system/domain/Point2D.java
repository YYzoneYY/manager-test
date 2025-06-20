package com.ruoyi.system.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Setter
@Getter
public class Point2D {
    private BigDecimal x;
    private BigDecimal y;
    private Long areaId;
    private String barType;

    private BigDecimal org_s_x;
    private BigDecimal org_s_y;
    private BigDecimal org_e_x;
    private BigDecimal org_e_y;

    public double distance(Point2D other) {
        BigDecimal dx = x.subtract(other.x);
        BigDecimal dy = y.subtract(other.y);
        return Math.sqrt(dx.multiply(dx).add(dy.multiply(dy)).doubleValue());
    }

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

    // equals 和 hashCode 用于 HashMap / HashSet 操作
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D)) return false;
        Point2D point = (Point2D) o;
        return x.compareTo(point.x) == 0 && y.compareTo(point.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x.stripTrailingZeros(), y.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "(" + x.toPlainString() + "," + y.toPlainString() + ")";
    }
}

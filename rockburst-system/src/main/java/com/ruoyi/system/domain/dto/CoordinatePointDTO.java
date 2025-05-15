package com.ruoyi.system.domain.dto;

import lombok.Data;

import java.util.Objects;

/**
 * @author: shikai
 * @date: 2025/5/14
 * @description:
 */

@Data
public class CoordinatePointDTO {
    public double x;
    public double y;

    public CoordinatePointDTO(double x, double y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoordinatePointDTO)) return false;
        CoordinatePointDTO point = (CoordinatePointDTO) o;
        return Double.compare(point.x, x) == 0 && Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
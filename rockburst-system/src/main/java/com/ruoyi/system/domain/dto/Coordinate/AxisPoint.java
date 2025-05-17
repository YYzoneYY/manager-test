package com.ruoyi.system.domain.dto.Coordinate;

import lombok.Data;

/**
 * @author: shikai
 * @date: 2025/5/16
 * @description: 实体类，表示一个二维点
 */

@Data
public class AxisPoint {
    public double x, y;
    public AxisPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
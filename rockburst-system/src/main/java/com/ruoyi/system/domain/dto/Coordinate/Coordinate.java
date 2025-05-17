package com.ruoyi.system.domain.dto.Coordinate;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/5/16
 * @description: Coordinate 实体类，表示一个四边形的四个点
 */

@Data
public class Coordinate {

    double x1, y1, x2, y2, x3, y3, x4, y4;

    // Coordinate 实体类，表示一个四边形的四个点
    public Coordinate(double x1, double y1, double x2, double y2,
                      double x3, double y3, double x4, double y4) {
        this.x1 = x1; this.y1 = y1;
        this.x2 = x2; this.y2 = y2;
        this.x3 = x3; this.y3 = y3;
        this.x4 = x4; this.y4 = y4;
    }

    // 转换为 AxisPoint 列表，便于处理为多边形
    public List<AxisPoint> toPointList() {
        return Arrays.asList(
                new AxisPoint(x1, y1),
                new AxisPoint(x2, y2),
                new AxisPoint(x3, y3),
                new AxisPoint(x4, y4)
        );
    }

}
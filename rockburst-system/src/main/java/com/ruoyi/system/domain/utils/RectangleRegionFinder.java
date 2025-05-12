package com.ruoyi.system.domain.utils;

import com.ruoyi.system.domain.Point2D;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RectangleRegionFinder {

    /**
     * 返回两条线段之间的最小距离（考虑四个点两两之间）
     */
    public static BigDecimal segmentDistance(List<Point2D> seg1, List<Point2D> seg2) {
        BigDecimal d1 = pointToSegmentDistance(seg1.get(0), seg2);
        BigDecimal d2 = pointToSegmentDistance(seg1.get(1), seg2);
        BigDecimal d3 = pointToSegmentDistance(seg2.get(0), seg1);
        BigDecimal d4 = pointToSegmentDistance(seg2.get(1), seg1);
        return Collections.min(Arrays.asList(d1, d2, d3, d4));
    }

    /**
     * 点到线段的距离
     */
    public static BigDecimal pointToSegmentDistance(Point2D p, List<Point2D> seg) {
        BigDecimal x = p.getX(), y = p.getY();
        BigDecimal x1 = seg.get(0).getX(), y1 = seg.get(0).getY();
        BigDecimal x2 = seg.get(1).getX(), y2 = seg.get(1).getY();

        BigDecimal dx = x2.subtract(x1), dy = y2.subtract(y1);
        BigDecimal lengthSquared = dx.multiply(dx).add(dy.multiply(dy));
        if (lengthSquared.compareTo(BigDecimal.ZERO) == 0)
            return x.subtract(x1).pow(2).add(y.subtract(y1).pow(2)).sqrt(new MathContext(20));

        BigDecimal t = (x.subtract(x1).multiply(dx).add(y.subtract(y1).multiply(dy)))
                .divide(lengthSquared, new MathContext(20));
        t = t.max(BigDecimal.ZERO).min(BigDecimal.ONE);

        BigDecimal projX = x1.add(dx.multiply(t));
        BigDecimal projY = y1.add(dy.multiply(t));
        return x.subtract(projX).pow(2).add(y.subtract(projY).pow(2)).sqrt(new MathContext(20));
    }

    /**
     * 主函数：输入线段列表，输出每条线段与最近线段组成的矩形
     */
    public static List<List<Point2D>> findRectangleRegions(List<List<Point2D>> segments) {
        List<List<Point2D>> rectangles = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            List<Point2D> segA = segments.get(i);
            BigDecimal minDist = null;
            List<Point2D> closestSeg = null;

            // 找出最近的一条线段
            for (int j = 0; j < segments.size(); j++) {
                if (i == j) continue;
                List<Point2D> segB = segments.get(j);
                BigDecimal dist = segmentDistance(segA, segB);
                if (minDist == null || dist.compareTo(minDist) < 0) {
                    minDist = dist;
                    closestSeg = segB;
                }
            }

            // 将两条线段的四个端点合并为一个矩形区域（不处理排序）
            if (closestSeg != null) {
                List<Point2D> rectangle = new ArrayList<>();
                rectangle.add(segA.get(0));
                rectangle.add(segA.get(1));
                rectangle.add(closestSeg.get(1));
                rectangle.add(closestSeg.get(0));
                rectangles.add(rectangle);
            }
        }

        return rectangles;
    }
}

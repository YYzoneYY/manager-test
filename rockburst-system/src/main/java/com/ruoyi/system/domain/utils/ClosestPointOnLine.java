package com.ruoyi.system.domain.utils;

import java.math.BigDecimal;

public class ClosestPointOnLine {

    // 输入 A, B, C, 以及点 K(x0, y0)
    public static BigDecimal[] getClosestPoint(BigDecimal A, BigDecimal B, BigDecimal C, BigDecimal x0, BigDecimal y0) {
        // 防止除零错误
        BigDecimal denominator = A.pow(2).add(B.pow(2));
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("A 和 B 不能同时为 0，这不是一条合法直线");
        }

        // 计算公式
        BigDecimal factor = (B.multiply(x0).add(A.multiply(y0)).add(C)).divide(denominator,10,BigDecimal.ROUND_DOWN);

        BigDecimal x = x0.subtract(B.multiply(factor));
        BigDecimal y = y0.subtract(A.multiply(factor));

        return new BigDecimal[]{x, y};
    }

//    // 测试一下
    public static void main(String[] args) {
        // 直线参数 Ax + By + C = 0
        double A = 0;
        double B = -0.01077;
        double C = 2598.9023607239;

        // 点 K(x0, y0)
        double x0 = 35.3725599086;
        double y0 = 108.8885426091;

        BigDecimal[] closestPoint = getClosestPoint(new BigDecimal(A), new BigDecimal(B), new BigDecimal(C), new BigDecimal(x0), new BigDecimal(y0));

        System.out.println("最近点坐标: (" + closestPoint[0] + ", " + closestPoint[1] + ")");
    }
}

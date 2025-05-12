package com.ruoyi.system.domain.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.ruoyi.system.domain.Point2D;
import com.ruoyi.system.domain.Segment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

public class GeometryUtil {

    /**
     * 判断点 (x, y) 是否在四边形 (p1, p2, p3, p4) 内（顺/逆时针均可）
     *
     * @param xa 点 x 坐标
     * @param ya 点 y 坐标
     * @param px 四边形的 4 个顶点 x 坐标数组（顺/逆时针）
     * @param py 四边形的 4 个顶点 y 坐标数组
     * @return true 表示在区域内
     */
    public static boolean isPointInQuadrilateral(
            String xa, String ya,
            String[] px, String[] py
    ) {
        BigDecimal x = new BigDecimal(xa);
        BigDecimal y = new BigDecimal(ya);

        BigDecimal[][] p = new BigDecimal[4][2];
        for (int i = 0; i < 4; i++) {
            p[i][0] = new BigDecimal(px[i]);
            p[i][1] = new BigDecimal(py[i]);
        }

        // 将四边形分为两个三角形，判断点是否在其中一个三角形内
        return isPointInTriangle(x, y, p[0], p[1], p[2]) ||
                isPointInTriangle(x, y, p[0], p[2], p[3]);
    }

    /**
     * 判断点是否在三角形内（使用面积法）
     */
    private static boolean isPointInTriangle(BigDecimal x, BigDecimal y,
                                             BigDecimal[] p1, BigDecimal[] p2, BigDecimal[] p3) {
        BigDecimal total = triangleArea(p1, p2, p3);
        BigDecimal a1 = triangleArea(new BigDecimal[]{x, y}, p2, p3);
        BigDecimal a2 = triangleArea(p1, new BigDecimal[]{x, y}, p3);
        BigDecimal a3 = triangleArea(p1, p2, new BigDecimal[]{x, y});

        // 判断面积之和是否等于总面积（允许极小误差）
        BigDecimal sum = a1.add(a2).add(a3);
        return total.subtract(sum).abs().compareTo(new BigDecimal("0.000001")) < 0;
    }

    /**
     * 计算三角形面积（海伦公式不稳定，这里使用行列式法）
     */
    private static BigDecimal triangleArea(BigDecimal[] p1, BigDecimal[] p2, BigDecimal[] p3) {
        return p1[0].multiply(p2[1].subtract(p3[1]))
                .add(p2[0].multiply(p3[1].subtract(p1[1])))
                .add(p3[0].multiply(p1[1].subtract(p2[1])))
                .abs()
                .divide(new BigDecimal("2.0"), 10, BigDecimal.ROUND_HALF_UP);
    }
    /**
     * 根据角度与距离计算延伸后的点坐标
     *
     * @param xa 原点 x 坐标（字符串）
     * @param ya 原点 y 坐标（字符串）
     * @param angleDeg 与 Y 轴正方向的夹角（单位：度）
     * @param i 距离（单位：米，可为负）
     * @param scale 坐标单位与米的比例（如 1 米 = 100 坐标单位，则 scale=100）
     * @return 延伸后点的坐标（BigDecimal[2]）
     */
    public static BigDecimal[] getExtendedPoint(
            String xa, String ya,
            double angleDeg,
            double i,
            double scale
    ) {
        BigDecimal x = new BigDecimal(xa);
        BigDecimal y = new BigDecimal(ya);

        // 转换为与 X 轴正方向的夹角（Math 以 X 正方向为 0°）
        double angleFromXAxis = 90 - angleDeg;

        // 弧度制
        double radians = Math.toRadians(angleFromXAxis);

        // 米 → 坐标单位
        double distanceInCoord = i * scale;

        // 计算偏移量
        double dx = distanceInCoord * Math.cos(radians);
        double dy = distanceInCoord * Math.sin(radians);

        // 新坐标
        BigDecimal newX = x.add(BigDecimal.valueOf(dx)).setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal newY = y.add(BigDecimal.valueOf(dy)).setScale(6, BigDecimal.ROUND_HALF_UP);

        return new BigDecimal[]{newX, newY};
    }


    /**
     *
     * @param bx1 第一个点的坐标x
     * @param by1 第一个点的坐标y
     * @param bx2 第二个点的坐标x
     * @param by2 第二个点的坐标y
     * @return
     */
    public static BigDecimal getDistance(BigDecimal bx1, BigDecimal by1, BigDecimal bx2, BigDecimal by2) {


        BigDecimal dx = bx2.subtract(bx1);
        BigDecimal dy = by2.subtract(by1);

        BigDecimal dx2 = dx.multiply(dx);
        BigDecimal dy2 = dy.multiply(dy);
        BigDecimal sum = dx2.add(dy2);

        // 使用 Math.sqrt，需转换为 double
        double result = Math.sqrt(sum.doubleValue());
        return new BigDecimal(result, MathContext.DECIMAL64).setScale(6, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算点 A(x1, y1) 到线段 MN(xm, ym)-(xn, yn) 的最近点坐标
     * 返回值为 BigDecimal[2]，分别是最近点的 x 和 y
     */
    /**
     *
     * @param x1Str 点的x坐标
     * @param y1Str 点的y坐标
     * @param xmStr 线段起点x
     * @param ymStr 线段起点y
     * @param xnStr 线段终点x
     * @param ynStr 线段终点y
     * @return
     */
    public static BigDecimal[] getClosestPointOnSegment(
            String x1Str, String y1Str,
            String xmStr, String ymStr,
            String xnStr, String ynStr
    ) {
        // 转为 BigDecimal
        BigDecimal x1 = new BigDecimal(x1Str);
        BigDecimal y1 = new BigDecimal(y1Str);
        BigDecimal xm = new BigDecimal(xmStr);
        BigDecimal ym = new BigDecimal(ymStr);
        BigDecimal xn = new BigDecimal(xnStr);
        BigDecimal yn = new BigDecimal(ynStr);

        // 向量 MA = A - M, 向量 MN = N - M
        BigDecimal dx = xn.subtract(xm);
        BigDecimal dy = yn.subtract(ym);

        BigDecimal lengthSquared = dx.multiply(dx).add(dy.multiply(dy)); // MN 向量长度平方

        if (lengthSquared.compareTo(BigDecimal.ZERO) == 0) {
            // M == N，退化为点
            return new BigDecimal[]{xm, ym};
        }

        // 计算投影比例 t = (MA·MN) / |MN|²
        BigDecimal mx = x1.subtract(xm);
        BigDecimal my = y1.subtract(ym);

        BigDecimal dot = mx.multiply(dx).add(my.multiply(dy)); // MA·MN
        BigDecimal t = dot.divide(lengthSquared, 10, BigDecimal.ROUND_HALF_UP); // 保留 10 位小数

        // 限制 t 在 [0, 1] 之间
        if (t.compareTo(BigDecimal.ZERO) < 0) {
            t = BigDecimal.ZERO;
        } else if (t.compareTo(BigDecimal.ONE) > 0) {
            t = BigDecimal.ONE;
        }

        // 最近点 P = M + t * (N - M)
        BigDecimal px = xm.add(t.multiply(dx));
        BigDecimal py = ym.add(t.multiply(dy));

        return new BigDecimal[]{px.setScale(6, BigDecimal.ROUND_HALF_UP), py.setScale(6, BigDecimal.ROUND_HALF_UP)};
    }

    /**
     * 二维数组字符串  返回二维数组
     * @param jsonStr
     * @return
     */
    public static List<BigDecimal[]> parseToBigDecimalArrayList(String jsonStr) {
        List<BigDecimal[]> result = new ArrayList<>();

        // 解析外层数组
        JSONArray outerArray = JSONUtil.parseArray(jsonStr);

        for (int i = 0; i < outerArray.size(); i++) {
            JSONArray innerArray = outerArray.getJSONArray(i);
            BigDecimal[] decimals = new BigDecimal[innerArray.size()];

            for (int j = 0; j < innerArray.size(); j++) {
                decimals[j] = innerArray.getBigDecimal(j);
            }

            result.add(decimals);
        }

        return result;
    }

    /**
     * 输入 "[m,n]"字符串 解析称 m n
     * @param input "[m,n]"
     * @return
     */
    public static BigDecimal[] parsePoint(String input) {
        if (input == null || !input.matches("\\[\\s*-?\\d+(\\.\\d+)?\\s*,\\s*-?\\d+(\\.\\d+)?\\s*\\]")) {
            throw new IllegalArgumentException("Invalid input format: " + input);
        }

        String[] parts = input.replaceAll("[\\[\\]\\s]", "").split(",");
        BigDecimal m = new BigDecimal(parts[0]);
        BigDecimal n = new BigDecimal(parts[1]);

        return new BigDecimal[]{m, n};
    }

    /**
     *
     * @param points 巷道的四个点
     * @param m 中心点x
     * @param n 中心点y
     * @return
     */
    public static List<BigDecimal[]> findTwoClosestPoints(List<BigDecimal[]> points, BigDecimal m, BigDecimal n) {
        List<Map.Entry<BigDecimal[], BigDecimal>> distances = new ArrayList<>();

        for (BigDecimal[] point : points) {
            BigDecimal dx = point[0].subtract(m);
            BigDecimal dy = point[1].subtract(n);
            BigDecimal distanceSquared = dx.pow(2).add(dy.pow(2));
            distances.add(new AbstractMap.SimpleEntry<>(point, distanceSquared));
        }

        // 排序
        distances.sort(Comparator.comparing(Map.Entry::getValue));

        // 取前两个点
        List<BigDecimal[]> result = new ArrayList<>();
        result.add(distances.get(0).getKey());
        result.add(distances.get(1).getKey());

        return result;
    }

    /**
     * 计算从 A(x1, y1) 到 B(x2, y2) 与 Y 轴正方向的夹角，单位为度，范围 [0, 360)
     */
    /**
     *
     * @param x1Str 线段起点x
     * @param y1Str 线段起点y
     * @param x2Str 线段终点x
     * @param y2Str 线段终点y
     * @return
     */
    public static BigDecimal calculateAngleFromYAxis(String x1Str, String y1Str, String x2Str, String y2Str) {
        // 转为 BigDecimal
        BigDecimal x1 = new BigDecimal(x1Str);
        BigDecimal y1 = new BigDecimal(y1Str);
        BigDecimal x2 = new BigDecimal(x2Str);
        BigDecimal y2 = new BigDecimal(y2Str);

        // 计算差值 dx = x2 - x1, dy = y2 - y1
        BigDecimal dx = x2.subtract(x1);
        BigDecimal dy = y2.subtract(y1);

        // 转为 double 计算方向角
        double dxDouble = dx.doubleValue();
        double dyDouble = dy.doubleValue();

        // 注意：与 Y轴正方向夹角，用 atan2(dx, dy)
        double angleRadians = Math.atan2(dxDouble, dyDouble);
        double angleDegrees = Math.toDegrees(angleRadians);

        // 保证角度在 [0, 360)
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }

        // 返回 BigDecimal 类型角度，保留6位小数
        return new BigDecimal(angleDegrees).setScale(6, BigDecimal.ROUND_HALF_UP);
    }


    /**
     * 转换成 Segment
     * @param x1 开始坐标x
     * @param y1 开始坐标y
     * @param x2 结束坐标x
     * @param y2 结束坐标y
     * @param meter 距离
     * @param areaId 危险区id
     * @return
     */
    public static Segment getSegment(String x1, String y1, String x2, String y2, String meter, Long areaId) {
        Point2D startPoint2D =  new Point2D(new BigDecimal(x1),new BigDecimal(y1));
        Point2D endPoint2D =  new Point2D(new BigDecimal(x2),new BigDecimal(y2));
        Segment segment = new Segment(startPoint2D,endPoint2D,new BigDecimal(meter),areaId);
        return segment;
    }


    /**
     * 坐标点与原点的直线距离
     * @param x
     * @param y
     * @param scale
     * @return
     */
    public static BigDecimal hypot(BigDecimal x, BigDecimal y, int scale) {
        BigDecimal xSquared = x.multiply(x);
        BigDecimal ySquared = y.multiply(y);
        BigDecimal sum = xSquared.add(ySquared);
        return sqrt(sum, scale);
    }

    public static BigDecimal sqrt(BigDecimal value, int scale) {
        BigDecimal two = BigDecimal.valueOf(2);
        BigDecimal x0 = BigDecimal.ZERO;
        BigDecimal x1 = new BigDecimal(Math.sqrt(value.doubleValue()));

        // 牛顿迭代法
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = value.divide(x0, scale, RoundingMode.HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(two, scale, RoundingMode.HALF_UP);
        }
        return x1;
    }
}

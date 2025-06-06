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


    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);

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
     * 获取离指定点 mn 最近的线段
     * @param segments List<Segment>
     * @param mn 坐标点 [x, y]
     * @return 最近的 Segment
     */
    public static Segment findNearestSegment(List<Segment> segments, BigDecimal[] mn) {
        if (mn == null || mn.length != 2) {
            throw new IllegalArgumentException("mn 必须是长度为 2 的数组");
        }

        Point2D p = new Point2D(mn[0], mn[1]);

        Segment closestSegment = null;
        BigDecimal minDistance = null;

        for (Segment segment : segments) {
            BigDecimal distance = pointToSegmentDistance(p, segment.getStart(), segment.getEnd());
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
                closestSegment = segment;
            }
        }

        return closestSegment;
    }

    /**
     * 计算点到线段的最短距离（平方计算后开根号）
     */
    public static BigDecimal pointToSegmentDistance(Point2D p, Point2D a, Point2D b) {
        BigDecimal dx = b.getX().subtract(a.getX());
        BigDecimal dy = b.getY().subtract(a.getY());

        BigDecimal lengthSquared = dx.pow(2).add(dy.pow(2));
        if (lengthSquared.compareTo(BigDecimal.ZERO) == 0) {
            // a == b, 退化为点
            return distance(p, a);
        }

        BigDecimal t = (p.getX().subtract(a.getX())).multiply(dx)
                .add(p.getY().subtract(a.getY()).multiply(dy))
                .divide(lengthSquared, 20, BigDecimal.ROUND_HALF_UP);

        if (t.compareTo(BigDecimal.ZERO) < 0) t = BigDecimal.ZERO;
        if (t.compareTo(BigDecimal.ONE) > 0) t = BigDecimal.ONE;

        BigDecimal projX = a.getX().add(t.multiply(dx));
        BigDecimal projY = a.getY().add(t.multiply(dy));

        return distance(p, new Point2D(projX, projY));
    }

    /**
     * 计算两点之间的距离
     */
    private static BigDecimal distance(Point2D p1, Point2D p2) {
        BigDecimal dx = p1.getX().subtract(p2.getX());
        BigDecimal dy = p1.getY().subtract(p2.getY());
        double dist = Math.sqrt(dx.pow(2).add(dy.pow(2)).doubleValue());
        return BigDecimal.valueOf(dist);
    }


    public static List<List<Point2D>> buildRegionsFromSortedSegments(List<Segment> sorted) {
        List<List<Point2D>> quyus = new ArrayList<>();

        for (int i = 0; i < sorted.size() - 1; i++) {
            Segment s1 = sorted.get(i);
            Segment s2 = sorted.get(i + 1);

            // 构成一个四边形区域：顺时针或逆时针都可以
            List<Point2D> region = new ArrayList<>();
            region.add(s1.getStart());
            region.add(s1.getEnd());
            region.add(s2.getEnd());
            region.add(s2.getStart());

            quyus.add(region);
        }

        return quyus;
    }


    public static List<Segment> findNearRectangleRegions(List<List<Point2D>> segments, Segment target) {
        List<Segment> segmentList = new ArrayList<>();

        for (List<Point2D> pts : segments) {
            if (pts.size() >= 2) {
                Segment s = new Segment(pts.get(0), pts.get(1), BigDecimal.ZERO);
                segmentList.add(s);
            }
        }

        segmentList.sort(Comparator.comparing(s -> GeometryUtil.segmentToSegmentDistance(s, target)));

        return segmentList;
    }

    public static BigDecimal pointToSegmentDistance(Point2D p, Segment s) {
        BigDecimal x = p.getX();
        BigDecimal y = p.getY();
        BigDecimal x1 = s.getStart().getX();
        BigDecimal y1 = s.getStart().getY();
        BigDecimal x2 = s.getEnd().getX();
        BigDecimal y2 = s.getEnd().getY();

        BigDecimal dx = x2.subtract(x1, MC);
        BigDecimal dy = y2.subtract(y1, MC);

        if (dx.compareTo(BigDecimal.ZERO) == 0 && dy.compareTo(BigDecimal.ZERO) == 0) {
            return sqrt(x.subtract(x1).pow(2).add(y.subtract(y1).pow(2)), MC);
        }

        BigDecimal tNumerator = (x.subtract(x1).multiply(dx)).add((y.subtract(y1).multiply(dy)));
        BigDecimal tDenominator = dx.pow(2).add(dy.pow(2));

        BigDecimal t = tNumerator.divide(tDenominator, MC);
        t = t.max(BigDecimal.ZERO).min(BigDecimal.ONE);

        BigDecimal projX = x1.add(t.multiply(dx));
        BigDecimal projY = y1.add(t.multiply(dy));

        System.out.println("projX = " + projX);
        System.out.println("projY = " + projY);
        BigDecimal ass = x.subtract(projX).pow(2).add(y.subtract(projY).pow(2));

        if(ass.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        return sqrt(x.subtract(projX).pow(2).add(y.subtract(projY).pow(2)), MC);
    }

    public static BigDecimal segmentToSegmentDistance(Segment s1, Segment s2) {
        BigDecimal d1 = pointToSegmentDistance(s1.getStart(), s2);
        BigDecimal d2 = pointToSegmentDistance(s1.getEnd(), s2);
        BigDecimal d3 = pointToSegmentDistance(s2.getStart(), s1);
        BigDecimal d4 = pointToSegmentDistance(s2.getEnd(), s1);
        return d1.min(d2).min(d3).min(d4);
    }

    private static BigDecimal sqrt(BigDecimal value, MathContext mc) {

        BigDecimal x = new BigDecimal(Math.sqrt(value.doubleValue()), mc);
//        System.out.println("x = " + x);
        return x.add(new BigDecimal(value.subtract(x.multiply(x, mc), mc).doubleValue() / (x.doubleValue() * 2.0), mc));
    }



    /**
         * 从 List<BigDecimal[]> 创建线段，并返回长度最短的两条
         * @param bigDecimals 共4个元素，每个元素是长度为2的数组 [x, y]
         * @return 最长的两个 Segment（降序排列）
         */
    public static Segment findShortestTwoSegments(List<BigDecimal[]> bigDecimals) {
        if (bigDecimals == null || bigDecimals.size() != 4) {
            throw new IllegalArgumentException("必须传入恰好4个 BigDecimal[] 点，每个数组长度为2");
        }

        List<Segment> segments = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            BigDecimal[] coords1 = bigDecimals.get(i);
            BigDecimal[] coords2 = bigDecimals.get((i + 1) % 4);

            Point2D start = new Point2D(coords1[0], coords1[1]);
            Point2D end = new Point2D(coords2[0], coords2[1]);
            BigDecimal interval = calculateDistance(coords1, coords2);

            segments.add(new Segment(start, end, interval));
        }

        // 按照 interval 降序排序
        segments.sort((s1, s2) -> s2.getInterval().compareTo(s1.getInterval()));

        return segments.get(3);
    }

    // 判断点 a 是否在 polygon 中（适用于四边形）
    public static boolean isInside(Point2D a, Point2D[] polygon) {
        int n = polygon.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            Point2D p1 = polygon[i];
            Point2D p2 = polygon[(i + 1) % n];

            if (intersects(a, p1, p2)) {
                count++;
            }
        }

        return count % 2 == 1;
    }

    // 判断 a 向右的射线是否和边 (p1, p2) 相交
    private static boolean intersects(Point2D a, Point2D p1, Point2D p2) {
        // 交换点使 p1.y <= p2.y
        if (p1.getY().compareTo(p2.getY()) > 0) {
            Point2D temp = p1;
            p1 = p2;
            p2 = temp;
        }

        BigDecimal ay = a.getY();
        BigDecimal ax = a.getX();

        // 如果点在顶点上，略微上移以避免精度误差
        if (ay.compareTo(p1.getY()) == 0 || ay.compareTo(p2.getY()) == 0) {
            ay = ay.add(new BigDecimal("0.000000001"));
        }

        // 不在边的垂直范围内
        if (ay.compareTo(p1.getY()) < 0 || ay.compareTo(p2.getY()) > 0) {
            return false;
        }

        // 射线右侧无交点
        if (ax.compareTo(p1.getX().max(p2.getX())) >= 0) {
            return false;
        }

        // 计算交点的 x 坐标
        BigDecimal dy = p2.getY().subtract(p1.getY());
        BigDecimal dx = p2.getX().subtract(p1.getX());

        if (dy.compareTo(BigDecimal.ZERO) == 0) {
            return false; // 水平边
        }

        BigDecimal k = dx.divide(dy, 20, BigDecimal.ROUND_HALF_UP); // 斜率
        BigDecimal xIntersect = p1.getX().add(k.multiply(ay.subtract(p1.getY())));

        return ax.compareTo(xIntersect) < 0;
    }


    //示例测试
    public static boolean getisInside(String x, String y,String fsx,String fsy,String fex,String fey,String ssx,String ssy,String sex,String sey ) {
        Point2D a = new Point2D(new BigDecimal(x), new BigDecimal(y));

        Point2D[] polygon = new Point2D[] {
                new Point2D(new BigDecimal(fsx), new BigDecimal(fsy)),
                new Point2D(new BigDecimal(fex), new BigDecimal(fey)),
                new Point2D(new BigDecimal(ssx), new BigDecimal(ssy)),
                new Point2D(new BigDecimal(sex), new BigDecimal(sey))
        };
        boolean result = isInside(a, polygon);
        return result;
    }
//
//    // 示例测试
//    public static void main(String[] args) {
//        Point2D a = new Point2D(new BigDecimal("3"), new BigDecimal("3"));
//
//        Point2D[] polygon = new Point2D[] {
//                new Point2D(new BigDecimal("1"), new BigDecimal("1")),
//                new Point2D(new BigDecimal("5"), new BigDecimal("1")),
//                new Point2D(new BigDecimal("4"), new BigDecimal("4")),
//                new Point2D(new BigDecimal("1"), new BigDecimal("4"))
//        };
//
//        boolean result = isInside(a, polygon);
//        System.out.println("点是否在四边形内: " + result);
//    }
//

    /**
     * 从 List<BigDecimal[]> 创建线段，并返回长度最长的两条
     * @param bigDecimals 共4个元素，每个元素是长度为2的数组 [x, y]
     * @return 最长的两个 Segment（降序排列）
     */
    public static List<Segment> findLongestTwoSegments(List<BigDecimal[]> bigDecimals) {
        if (bigDecimals == null || bigDecimals.size() != 4) {
            throw new IllegalArgumentException("必须传入恰好4个 BigDecimal[] 点，每个数组长度为2");
        }

        List<Segment> segments = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            BigDecimal[] coords1 = bigDecimals.get(i);
            BigDecimal[] coords2 = bigDecimals.get((i + 1) % 4);

            Point2D start = new Point2D(coords1[0], coords1[1]);
            Point2D end = new Point2D(coords2[0], coords2[1]);
            BigDecimal interval = calculateDistance(coords1, coords2);

            segments.add(new Segment(start, end, interval));
        }

        // 按照 interval 降序排序
        segments.sort((s1, s2) -> s2.getInterval().compareTo(s1.getInterval()));

        return segments.subList(0, 2);
    }



    // 计算两点间距离
    public static BigDecimal calculateDistance(BigDecimal[] p1, BigDecimal[] p2) {
        BigDecimal dx = p1[0].subtract(p2[0]);
        BigDecimal dy = p1[1].subtract(p2[1]);
        double dist = Math.sqrt(dx.pow(2).add(dy.pow(2)).doubleValue());
        return BigDecimal.valueOf(dist);
    }

    /**
     * 判断在指定方向角下，点 A 和 B 的前后顺序
     *
     * @param xa 点 A 的 X 坐标（BigDecimal）
     * @param ya 点 A 的 Y 坐标（BigDecimal）
     * @param xb 点 B 的 X 坐标（BigDecimal）
     * @param yb 点 B 的 Y 坐标（BigDecimal）
     * @param towardAngleDeg 与 Y 轴的夹角，单位为度（0 表示正北方向，顺时针为正）
     * @return "A → B" 表示 A 在前，"B → A" 表示 B 在前，重合则返回提示
     */
    public static BigDecimal[] getOrder(BigDecimal xa, BigDecimal ya, BigDecimal xb, BigDecimal yb, double towardAngleDeg) {
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP); // 精度可调

        // 角度转换为弧度（用 double 计算即可）
        double theta = Math.toRadians(towardAngleDeg);
        BigDecimal dx = new BigDecimal(Math.sin(theta), mc);
        BigDecimal dy = new BigDecimal(Math.cos(theta), mc);

        // 向量 AB = (xb - xa, yb - ya)
        BigDecimal abx = xb.subtract(xa);
        BigDecimal aby = yb.subtract(ya);

        // 点积 dot = abx * dx + aby * dy
        BigDecimal dot = abx.multiply(dx, mc).add(aby.multiply(dy, mc), mc);

        int cmp = dot.compareTo(BigDecimal.ZERO);
        if (cmp > 0) {
            BigDecimal[] sge = new BigDecimal[4];
            sge[0] = xa;
            sge[1] = ya;
            sge[2] = xb;
            sge[3] = ya;
            return sge;
        } else if (cmp < 0) {
            BigDecimal[] sge = new BigDecimal[4];
            sge[0] = xb;
            sge[1] = yb;
            sge[2] = xa;
            sge[3] = ya;
            return sge;
        } else {
            BigDecimal[] sge = new BigDecimal[4];
            sge[0] = xa;
            sge[1] = ya;
            sge[2] = xb;
            sge[3] = ya;
            return sge;
        }
    }

    /**
     * 计算点m到直线ab的垂线方向与Y轴正方向夹角
     * @param a1
     * @param a2
     * @param m
     * @return
     */
    public static double angleWithYAxisOfPerpendicular(BigDecimal[] a1, BigDecimal[] a2, BigDecimal[] m) {
        double x1 = a1[0].doubleValue(), y1 = a1[1].doubleValue();
        double x2 = a2[0].doubleValue(), y2 = a2[1].doubleValue();
        double x0 = m[0].doubleValue(), y0 = m[1].doubleValue();

        double dx = x2 - x1;
        double dy = y2 - y1;

        // 计算垂足 P 到 m 的方向向量
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) {
            // a1 == a2，线段退化成点
            dx = x1 - x0;
            dy = y1 - y0;
        } else {
            double t = ((x0 - x1) * dx + (y0 - y1) * dy) / len2;

            double px = x1 + t * dx;
            double py = y1 + t * dy;

            dx = px - x0;
            dy = py - y0;
        }

        // 求夹角：向量 (dx, dy) 与 y轴正方向 (0,1) 的夹角
        double vlen = Math.sqrt(dx * dx + dy * dy);
        if (vlen == 0) return 0.0; // 点在直线上，方向不确定，返回0°

        double cosTheta = dy / vlen;
        double thetaRad = Math.acos(cosTheta); // ∈ [0, π]
        return Math.toDegrees(thetaRad);       // 返回角度 ∈ [0°, 180°]
    }

    /**
     * n个点距离mn最近的点
     * @param points
     * @param mn
     * @return
     */
    public static BigDecimal[] findNearestPoint(BigDecimal[][] points, BigDecimal[] mn) {
        int nearestIndex = -1;
        BigDecimal minDistance = null;

        for (int i = 0; i < points.length; i++) {
            BigDecimal distance = calculateDistance(points[i], mn);
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
                nearestIndex = i;
            }
        }
        return points[nearestIndex]; // 返回最近点的索引
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

    public static BigDecimal[] parsePoints(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input is null");
        }

        // 去掉中括号和多余空格
        input = input.trim().replaceAll("[\\[\\]]", "");

        // 拆分逗号
        String[] parts = input.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid input format: " + input);
        }

        try {
            BigDecimal m = new BigDecimal(parts[0].trim());
            BigDecimal n = new BigDecimal(parts[1].trim());
            return new BigDecimal[]{m, n};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in input: " + input, e);
        }
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

        /**
     * 计算两条线段的交点
     * @param p1 第一条线段的起点
     * @param p2 第一条线段的终点
     * @param p3 第二条线段的起点
     * @param p4 第二条线段的终点
     * @return 若相交，返回交点坐标；否则返回 null
     */
        public static Point2D getIntersection(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
            MathContext mc = new MathContext(20, RoundingMode.HALF_UP); // 高精度上下文
            BigDecimal epsilon = new BigDecimal("1e-10"); // 容差判断边界误差

            // 提取坐标
            BigDecimal x1 = p1.getX(), y1 = p1.getY();
            BigDecimal x2 = p2.getX(), y2 = p2.getY();
            BigDecimal x3 = p3.getX(), y3 = p3.getY();
            BigDecimal x4 = p4.getX(), y4 = p4.getY();

            // 向量差
            BigDecimal dx1 = x2.subtract(x1, mc);
            BigDecimal dy1 = y2.subtract(y1, mc);
            BigDecimal dx2 = x4.subtract(x3, mc);
            BigDecimal dy2 = y4.subtract(y3, mc);

            // 计算行列式
            BigDecimal denominator = dx1.multiply(dy2, mc).subtract(dy1.multiply(dx2, mc), mc);
            if (denominator.abs().compareTo(epsilon) < 0) {
                return null; // 平行或重合
            }

            BigDecimal dx3 = x1.subtract(x3, mc);
            BigDecimal dy3 = y1.subtract(y3, mc);

            // 参数 t 和 u
            BigDecimal tNumerator = dx3.multiply(dy2, mc).subtract(dy3.multiply(dx2, mc), mc);
            BigDecimal uNumerator = dx3.multiply(dy1, mc).subtract(dy3.multiply(dx1, mc), mc);

            BigDecimal t = tNumerator.divide(denominator, mc);
            BigDecimal u = uNumerator.divide(denominator, mc);

            // 使用容差判断是否在线段上
            if (t.compareTo(BigDecimal.ZERO.subtract(epsilon)) >= 0 && t.compareTo(BigDecimal.ONE.add(epsilon)) <= 0 &&
                    u.compareTo(BigDecimal.ZERO.subtract(epsilon)) >= 0 && u.compareTo(BigDecimal.ONE.add(epsilon)) <= 0) {

                BigDecimal intersectX = x1.add(dx1.multiply(t, mc), mc);
                BigDecimal intersectY = y1.add(dy1.multiply(t, mc), mc);
                return new Point2D(intersectX, intersectY);
            }

            return null;
        }


//    /**
//     * 计算两条线段的交点
//     * @param p1 第一条线段的起点
//     * @param p2 第一条线段的终点
//     * @param p3 第二条线段的起点
//     * @param p4 第二条线段的终点
//     * @return 若相交，返回交点坐标；否则返回 null
//     */
//    public static Point2D getIntersection(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
//        MathContext mc = new MathContext(6, RoundingMode.HALF_UP); // 控制精度和舍入方式
//
//        // 取出各点坐标
//        BigDecimal x1 = p1.getX(), y1 = p1.getY();
//        BigDecimal x2 = p2.getX(), y2 = p2.getY();
//        BigDecimal x3 = p3.getX(), y3 = p3.getY();
//        BigDecimal x4 = p4.getX(), y4 = p4.getY();
//
//        // 计算两个方向向量
//        BigDecimal dx1 = x2.subtract(x1, mc);
//        BigDecimal dy1 = y2.subtract(y1, mc);
//        BigDecimal dx2 = x4.subtract(x3, mc);
//        BigDecimal dy2 = y4.subtract(y3, mc);
//
//        // 计算分母，判断是否平行（行列式为0）
//        BigDecimal denominator = dx1.multiply(dy2, mc).subtract(dy1.multiply(dx2, mc), mc);
//        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
//            return null; // 两线段平行或重合
//        }
//
//        // 线段间向量差值
//        BigDecimal dx3 = x1.subtract(x3, mc);
//        BigDecimal dy3 = y1.subtract(y3, mc);
//
//        // 计算参数 t 和 u 的分子（参数化方程）
//        BigDecimal tNumerator = dx3.multiply(dy2, mc).subtract(dy3.multiply(dx2, mc), mc);
//        BigDecimal uNumerator = dx3.multiply(dy1, mc).subtract(dy3.multiply(dx1, mc), mc);
//
//        // 计算 t 和 u，用于判断交点是否在线段范围内
//        BigDecimal t = tNumerator.divide(denominator, mc);
//        BigDecimal u = uNumerator.divide(denominator, mc);
//
//        // 如果交点在线段的范围内（0 <= t <= 1 且 0 <= u <= 1）
//        if (t.compareTo(BigDecimal.ZERO) >= 0 && t.compareTo(BigDecimal.ONE) <= 0 &&
//                u.compareTo(BigDecimal.ZERO) >= 0 && u.compareTo(BigDecimal.ONE) <= 0) {
//
//            // 利用参数 t 计算交点坐标
//            BigDecimal intersectX = x1.add(dx1.multiply(t, mc), mc);
//            BigDecimal intersectY = y1.add(dy1.multiply(t, mc), mc);
//            return new Point2D(intersectX, intersectY);
//        }
//
//        return null; // 不在线段内相交
//    }

    /**
     * 计算四边形的中心点坐标
     * 输入必须是 4 个点（按任意顺序）
     * 中心点为四个点坐标的平均值
     *
     * @param points 由 4 个 Point2D 对象组成的列表
     * @return 中心点 Point2D 对象
     */
    public static Point2D getCenterPoint(List<Point2D> points) {
        // 校验点数量是否为 4
        if (points.size() != 4) {
            throw new IllegalArgumentException("必须传入 4 个点");
        }

        // 用于累加 x 和 y 坐标
        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;

        // 遍历每个点，累加它们的 x 和 y 坐标
        for (Point2D p : points) {
            sumX = sumX.add(p.getX());
            sumY = sumY.add(p.getY());
        }

        // 计算平均值：x 坐标之和除以 4，y 坐标之和除以 4
        BigDecimal centerX = sumX.divide(BigDecimal.valueOf(4), MathContext.DECIMAL64);
        BigDecimal centerY = sumY.divide(BigDecimal.valueOf(4), MathContext.DECIMAL64);

        // 返回中心点对象
        return new Point2D(centerX, centerY);
    }

    /**
     * 判断点 P 是否在线段 AB 上
     * @param p 点p
     * @param a 线段AB 的点a
     * @param b 线段AB 的点b
     * @return
     */
    public static boolean isPointOnSegment(Point2D p, Point2D a, Point2D b) {
        BigDecimal x = p.getX(), y = p.getY();
        BigDecimal x1 = a.getX(), y1 = a.getY();
        BigDecimal x2 = b.getX(), y2 = b.getY();

        // 计算叉积：如果为0说明共线
        BigDecimal cross = (x.subtract(x1)).multiply(y2.subtract(y1))
                .subtract((y.subtract(y1)).multiply(x2.subtract(x1)));

        if (cross.abs().compareTo(new BigDecimal("1e-10")) > 0) {
            return false; // 不共线
        }

        // 判断是否在端点范围内
        BigDecimal minX = x1.min(x2), maxX = x1.max(x2);
        BigDecimal minY = y1.min(y2), maxY = y1.max(y2);

        return x.compareTo(minX) >= 0 && x.compareTo(maxX) <= 0 &&
                y.compareTo(minY) >= 0 && y.compareTo(maxY) <= 0;
    }



    public static void main(String[] args) {
        Point2D p1 = new Point2D(new BigDecimal("0"), new BigDecimal("0"));
        Point2D p2 = new Point2D(new BigDecimal("4"), new BigDecimal("4"));
        Point2D p3 = new Point2D(new BigDecimal("0"), new BigDecimal("4"));
        Point2D p4 = new Point2D(new BigDecimal("4"), new BigDecimal("0"));

        Point2D result = getIntersection(p1, p2, p3, p4);
        if (result != null) {
            System.out.println("交点坐标为: " + result);
        } else {
            System.out.println("两线段不相交");
        }
    }

}

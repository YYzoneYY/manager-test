
package com.ruoyi.web.controller.basicInfo;

import com.ruoyi.system.domain.Point2D;

import java.math.BigDecimal;

public class SegmentIntersection  {

    public static Point2D getIntersection(Point2D a, Point2D b, Point2D c, Point2D d) {
        BigDecimal[] u = vsub(b, a);
        BigDecimal[] v = vsub(d, c);
        BigDecimal[] w = vsub(a, c);

        BigDecimal dVal = vperp(u, v);
        if (dVal.abs().compareTo(new BigDecimal("1e-12")) < 0) {
            return null; // 平行或重合
        }

        BigDecimal s = vperp(v, w).divide(dVal, 20, BigDecimal.ROUND_HALF_UP);
        BigDecimal t = vperp(u, w).divide(dVal, 20, BigDecimal.ROUND_HALF_UP);

        // 判断 s 和 t 是否都在 [0, 1] 区间内
        if (isInRange(s) && isInRange(t)) {
            // 交点 = a + u * s
            BigDecimal ix = a.getX().add(u[0].multiply(s));
            BigDecimal iy = a.getY().add(u[1].multiply(s));
            return new Point2D(ix, iy);
        }

        return null; // 不在线段内相交
    }

    private static BigDecimal[] vsub(Point2D p1, Point2D p2) {
        return new BigDecimal[]{
                p1.getX().subtract(p2.getX()),
                p1.getY().subtract(p2.getY())
        };
    }

    private static BigDecimal vperp(BigDecimal[] a, BigDecimal[] b) {
        return a[0].multiply(b[1]).subtract(a[1].multiply(b[0]));
    }

    private static boolean isInRange(BigDecimal val) {
        return val.compareTo(BigDecimal.ZERO) >= 0 && val.compareTo(BigDecimal.ONE) <= 0;
    }

    public static void main(String[] args) {
        Point2D a = new Point2D(new BigDecimal("1303.164884"), new BigDecimal("2320.113666"));
        Point2D b = new Point2D(new BigDecimal("1303.165378"), new BigDecimal("2203.113669"));
        Point2D c = new Point2D(new BigDecimal("-21.086016"), new BigDecimal("2255.609825"));
        Point2D d = new Point2D(new BigDecimal("3559.891033"), new BigDecimal("2255.621923"));

        Point2D intersection = SegmentIntersection.getIntersection(a, b, c, d);
        if (intersection != null) {
            System.out.println("交点: (" + intersection.getX() + ", " + intersection.getY() + ")");
        } else {
            System.out.println("线段不相交");
        }
    }
}

package com.ruoyi.system.domain.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.BizTunnelBar;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.domain.dto.Coordinate.AxisPoint;
import com.ruoyi.system.domain.dto.CoordinatePointDTO;
import com.ruoyi.system.mapper.BizTravePointMapper;
import com.ruoyi.system.mapper.BizTunnelBarMapper;
import com.ruoyi.system.mapper.SysConfigMapper;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author: shikai
 * @date: 2025/5/14
 * @description:
 */
public class AlgorithmUtils {

    /**
     * 通过 导线点+距离 获取相应坐标
     * @param workFaceId 工作面id
     * @param tunnelId 巷道id
     * @param pointId 导线点
     * @param distance 距离
     * @return 坐标
     */
    public static String obtainCoordinate(Long workFaceId, Long tunnelId, Long pointId, String distance,
                                          BizTunnelBarMapper bizTunnelBarMapper,
                                          BizTravePointMapper bizTravePointMapper, SysConfigMapper sysConfigMapper) {
        String coordinate = "";
        BizTravePoint bizTravePoint = bizTravePointMapper.selectOne(new LambdaQueryWrapper<BizTravePoint>()
                .eq(BizTravePoint::getPointId, pointId));
        String axisx = bizTravePoint.getAxisx();
        String axisy = bizTravePoint.getAxisy();
        BizTunnelBar bizTunnelBar = bizTunnelBarMapper.selectOne(new LambdaQueryWrapper<BizTunnelBar>()
                .eq(BizTunnelBar::getWorkfaceId, workFaceId)
                .eq(BizTunnelBar::getTunnelId, tunnelId)
                .last("LIMIT 1"));
        // 巷道走向
        Double towardAngle = bizTunnelBar.getTowardAngle();
        // 比例
        String key = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, "bili"))
                .getConfigValue();
        BigDecimal[] extendedPoint = GeometryUtil.getExtendedPoint(axisx, axisy, towardAngle, Double.parseDouble(distance), Double.parseDouble(key));
        coordinate = extendedPoint[0] + "," + extendedPoint[1];
        return coordinate;
    }

     /**
     * 判断目标点是否在由给定的一组无序点所构成的多边形内部
     * @param polygonPoints 多边形顶点（无序）
     * @param targetPoint 要判断的目标点
     * @return 是否在多边形内
     */
    public static boolean isPointInPolygon(List<CoordinatePointDTO> polygonPoints, CoordinatePointDTO targetPoint) {
        if (polygonPoints == null || polygonPoints.size() < 3) {
            throw new IllegalArgumentException("至少需要三个点来构成区域");
        }

        // 去重处理（避免重复点干扰排序）
        Set<CoordinatePointDTO> uniquePoints = new LinkedHashSet<>(polygonPoints);
        List<CoordinatePointDTO> sortedPoints = sortPoints(new ArrayList<>(uniquePoints));

        // 如果去重后只剩1个或2个点，无法构成有效区域
        if (sortedPoints.size() < 3) {
            return false;
        }

        return isInsidePolygon(sortedPoints, targetPoint);
    }

    /**
     * 判断一个点是否在多边形内部（射线法）
     */
    public static boolean isPointInPolygon(AxisPoint p, List<AxisPoint> polygon) {
        int count = 0;
        for (int i = 0; i < polygon.size(); i++) {
            AxisPoint a = polygon.get(i);
            AxisPoint b = polygon.get((i + 1) % polygon.size());
            if (rayIntersectsSegment(p, a, b)) {
                count++;
            }
        }
        return count % 2 == 1;
    }


    /**
     * 判断两条线段是否相交
     */
    public static boolean segmentsIntersect(AxisPoint a1, AxisPoint a2, AxisPoint b1, AxisPoint b2) {
        return ccw(a1, b1, b2) != ccw(a2, b1, b2) &&
                ccw(a1, a2, b1) != ccw(a1, a2, b2);
    }

    /**
     * 判断两个多边形是否存在边相交
     */
    public static boolean polygonsIntersect(List<AxisPoint> poly1, List<AxisPoint> poly2) {
        for (int i = 0; i < poly1.size(); i++) {
            AxisPoint a1 = poly1.get(i);
            AxisPoint a2 = poly1.get((i + 1) % poly1.size());
            for (int j = 0; j < poly2.size(); j++) {
                AxisPoint b1 = poly2.get(j);
                AxisPoint b2 = poly2.get((j + 1) % poly2.size());
                if (segmentsIntersect(a1, a2, b1, b2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断 polygon outer 是否完全包含 polygon inner
     */
    public static boolean polygonContainsPolygon(List<AxisPoint> outer, List<AxisPoint> inner) {
        for (AxisPoint p : inner) {
            if (!isPointInPolygon(p, outer)) return false;
        }
        return true;
    }




    /**
     * 按极角排序，构造闭合多边形轮廓
     */
    private static List<CoordinatePointDTO> sortPoints(List<CoordinatePointDTO> points) {
        if (points.size() < 2) return points;

        // 找到最左下角的点作为基准点
        CoordinatePointDTO base = Collections.min(points,
                Comparator.comparingDouble((CoordinatePointDTO p) -> p.y).thenComparing(p -> p.x));

        points.sort((p1, p2) -> {
            double cp = cross(base, p1, p2);
            if (cp != 0) return cp > 0 ? -1 : 1;
            else {
                double d1 = (p1.x - base.x) * (p1.x - base.x) + (p1.y - base.y) * (p1.y - base.y);
                double d2 = (p2.x - base.x) * (p2.x - base.x) + (p2.y - base.y) * (p2.y - base.y);
                return Double.compare(d1, d2);
            }
        });

        return points;
    }

    /**
     * 向量叉积：(b - a) × (c - a)
     */
    private static double cross(CoordinatePointDTO a, CoordinatePointDTO b, CoordinatePointDTO c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    /**
     * 射线法判断点是否在多边形内
     */
    private static boolean isInsidePolygon(List<CoordinatePointDTO> polygon, CoordinatePointDTO point) {
        int n = polygon.size();
        int intersectCount = 0;
        for (int i = 0; i < n; i++) {
            CoordinatePointDTO a = polygon.get(i);
            CoordinatePointDTO b = polygon.get((i + 1) % n);
            if (rayIntersectsSegment(point, a, b)) {
                intersectCount++;
            }
        }
        return (intersectCount % 2) == 1;
    }

    /**
     * 判断射线是否与线段相交
     */
    private static boolean rayIntersectsSegment(CoordinatePointDTO p, CoordinatePointDTO a, CoordinatePointDTO b) {
        if (a.y > b.y) {
            CoordinatePointDTO temp = a;
            a = b;
            b = temp;
        }
        if (p.y == a.y || p.y == b.y) {
            p = new CoordinatePointDTO(p.x, p.y + 0.0001); // 避免边界重合
        }
        if (p.y < a.y || p.y > b.y) return false;
        if (p.x > Math.max(a.x, b.x)) return false;
        if (p.x < Math.min(a.x, b.x)) return true;

        double redX = (p.y - a.y) * (b.x - a.x) / (b.y - a.y) + a.x;
        return p.x < redX;
    }


    /**
     *  射线法核心：判断点 p 是否穿过线段 ab
     */
    private static boolean rayIntersectsSegment(AxisPoint p, AxisPoint a, AxisPoint b) {
        if (a.y > b.y) {
            AxisPoint temp = a; a = b; b = temp;
        }
        if (p.y == a.y || p.y == b.y) p.y += 1e-10;
        if (p.y < a.y || p.y > b.y) return false;
        if (p.x >= Math.max(a.x, b.x)) return false;
        if (p.x < Math.min(a.x, b.x)) return true;

        double xinters = (p.y - a.y) * (b.x - a.x) / (b.y - a.y) + a.x;
        return p.x < xinters;
    }

    /**
     * 计算点的相对方向，判断是否构成逆时针
     */
    private static boolean ccw(AxisPoint a, AxisPoint b, AxisPoint c) {
        return (c.y - a.y) * (b.x - a.x) > (b.y - a.y) * (c.x - a.x);
    }
}
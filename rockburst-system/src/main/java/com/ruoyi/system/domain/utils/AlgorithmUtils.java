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

    // 使用更大的容差值，避免浮点数精度问题
    private static final double EPSILON = 1e-4;
    // 明确内部点的最小距离阈值
    private static final double CLEAR_INSIDE_THRESHOLD = 0.01;

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
     * 判断点是否在多边形内部（增强版）
     */
    public static boolean isPointInPolygonPro(AxisPoint p, List<AxisPoint> polygon) {
        return isPointClearlyInside(p, polygon);
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
     * 判断 polygon outer 是否完全包含 polygon inner
     * 优化后的方法，考虑了边界情况和浮点精度
     */
    public static boolean polygonContainsPolygon(List<AxisPoint> outer, List<AxisPoint> inner) {
        // 首先检查是否有边相交
        if (polygonsIntersect(outer, inner)) {
            return false;
        }

        // 检查inner的所有顶点是否都在outer内部
        for (AxisPoint p : inner) {
            if (!isPointClearlyInside(p, outer)) {
                return false;
            }
        }

        // 额外检查outer是否完全包围inner（防止inner部分在outer外的情况）
        return isPolygonCompletelyOutside(outer, inner);
    }

    /**
     * 判断点是否明确在多边形内部（考虑安全距离）
     */
    public static boolean isPointClearlyInside(AxisPoint p, List<AxisPoint> polygon) {
        if (!isPointInPolygon(p, polygon)) {
            return false;
        }

        // 计算点到多边形各边的最小距离
        double minDistance = Double.MAX_VALUE;
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            AxisPoint a = polygon.get(i);
            AxisPoint b = polygon.get((i + 1) % n);
            double dist = distanceToSegment(p, a, b);
            minDistance = Math.min(minDistance, dist);
        }

        // 如果点离边太近，不认为是明确在内部
        return minDistance >= CLEAR_INSIDE_THRESHOLD;
    }

    /**
     * 调整点坐标以避免与多边形顶点Y坐标相同的情况
     */
    private static AxisPoint adjustPointForRayCasting(AxisPoint p, List<AxisPoint> polygon) {
        for (AxisPoint vertex : polygon) {
            if (Math.abs(p.y - vertex.y) < EPSILON) {
                return new AxisPoint(p.x, p.y + EPSILON * 2);
            }
        }
        return p;
    }


    /**
     * 判断点是否在多边形内部（射线法）
     */
    private static boolean isPointInPolygon(AxisPoint p, List<AxisPoint> polygon) {
        int count = 0;
        int n = polygon.size();

        // 处理浮点精度问题
        AxisPoint adjustedP = adjustPointForRayCasting(p, polygon);

        for (int i = 0; i < n; i++) {
            AxisPoint a = polygon.get(i);
            AxisPoint b = polygon.get((i + 1) % n);
            if (rayIntersectsSegment(adjustedP, a, b)) {
                count++;
            }
        }

        return count % 2 == 1;
    }


    /**
     * 射线法核心：判断点 p 是否穿过线段 ab
     */
    private static boolean rayIntersectsSegment(AxisPoint p, AxisPoint a, AxisPoint b) {
        // 确保a在b下方
        if (a.y > b.y) {
            AxisPoint temp = a;
            a = b;
            b = temp;
        }

        // 如果点在线的Y范围外，肯定不相交
        if (p.y < a.y - EPSILON || p.y > b.y + EPSILON) {
            return false;
        }

        // 如果点在线的右侧，肯定不相交
        if (p.x >= Math.max(a.x, b.x) + EPSILON) {
            return false;
        }

        // 如果点在两条端点的左侧，肯定相交
        if (p.x <= Math.min(a.x, b.x) - EPSILON) {
            return true;
        }

        // 计算交点X坐标
        double xIntersect = (p.y - a.y) * (b.x - a.x) / (b.y - a.y) + a.x;
        return p.x <= xIntersect + EPSILON;
    }

    /**
     * 计算点到线段的最短距离
     */
    private static double distanceToSegment(AxisPoint p, AxisPoint a, AxisPoint b) {
        double x = p.x, y = p.y;
        double x1 = a.x, y1 = a.y;
        double x2 = b.x, y2 = b.y;

        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = (lenSq != 0) ? dot / lenSq : -1;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        return Math.sqrt((x - xx) * (x - xx) + (y - yy) * (y - yy));
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
     * 判断两条线段是否相交
     */
    public static boolean segmentsIntersect(AxisPoint a1, AxisPoint a2, AxisPoint b1, AxisPoint b2) {
        int ccw1 = ccw(a1, a2, b1);
        int ccw2 = ccw(a1, a2, b2);
        int ccw3 = ccw(b1, b2, a1);
        int ccw4 = ccw(b1, b2, a2);

        // 标准线段相交检查
        if ((ccw1 * ccw2 < 0) && (ccw3 * ccw4 < 0)) {
            return true;
        }

        // 检查共线情况
        if (ccw1 == 0 && isOnSegment(a1, a2, b1)) return true;
        if (ccw2 == 0 && isOnSegment(a1, a2, b2)) return true;
        if (ccw3 == 0 && isOnSegment(b1, b2, a1)) return true;
        if (ccw4 == 0 && isOnSegment(b1, b2, a2)) return true;

        return false;
    }

    /**
     * 计算三点相对方向（改进版，返回-1,0,1）
     */
    private static int ccw(AxisPoint a, AxisPoint b, AxisPoint c) {
        double area = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (Math.abs(area) < EPSILON) return 0;  // 共线
        return (area > 0) ? 1 : -1;  // 逆时针或顺时针
    }

    /**
     * 检查点c是否在线段ab上
     */
    private static boolean isOnSegment(AxisPoint a, AxisPoint b, AxisPoint c) {
        return Math.min(a.x, b.x) - EPSILON <= c.x && c.x <= Math.max(a.x, b.x) + EPSILON &&
                Math.min(a.y, b.y) - EPSILON <= c.y && c.y <= Math.max(a.y, b.y) + EPSILON;
    }

    /**
     * 检查inner是否完全在outer外部
     */
    private static boolean isPolygonCompletelyOutside(List<AxisPoint> outer, List<AxisPoint> inner) {
        for (AxisPoint p : outer) {
            if (isPointInPolygon(p, inner)) {
                return false;
            }
        }
        return true;
    }
}
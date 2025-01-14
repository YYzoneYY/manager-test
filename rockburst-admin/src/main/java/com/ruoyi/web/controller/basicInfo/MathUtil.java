package com.ruoyi.web.controller.basicInfo;

import com.ruoyi.system.domain.BizTravePoint;
import com.ruoyi.system.domain.Point;

import java.math.BigDecimal;

public class MathUtil {


    public static Double getDistance(BizTravePoint a1, BizTravePoint a2){
        return getMath(zhuanhuan(a1),zhuanhuan(a2));
    }

    public static BizTravePoint getMinDistance(BizTravePoint a, BizTravePoint b1, BizTravePoint b2){
        BizTravePoint entity = new BizTravePoint();
        Double q1 = getDistance(a,b1);
        Double q2 = getDistance(a,b2);
        if(q1 != null && q2 != null && q1 < q2){
            entity.setPointId(b1.getPointId()).setDistance(q1);
            return entity;
        }else {
            entity.setPointId(b2.getPointId()).setDistance(q2);
            return entity;
        }
    }


    private static Double getMath(Point a, Point n){
        BigDecimal ax = a.getX().subtract(n.getX());
        BigDecimal ay = a.getY().subtract(n.getY());
        BigDecimal az = a.getZ().subtract(n.getZ());

        ax = ax.multiply(ax);
        ay = ay.multiply(ay);
        az = az.multiply(az);

        Double an = Math.sqrt(ax.add(ay).add(az).doubleValue());
        return an;
    }

    private static Point zhuanhuan(BizTravePoint bizTravePoint){
        Point point = new Point();
        point.setX(new BigDecimal(bizTravePoint.getAxisx()));
        point.setY(new BigDecimal(bizTravePoint.getAxisy()));
        point.setZ(new BigDecimal(bizTravePoint.getAxisz()));
        return point;
    }
}

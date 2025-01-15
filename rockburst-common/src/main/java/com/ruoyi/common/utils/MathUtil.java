package com.ruoyi.common.utils;

import java.math.BigDecimal;

public class MathUtil {


    public static Double getDistance(String ax, String ay, String az,
                                     String bx, String by, String bz){
        return getMath(zhuanhuan(ax),zhuanhuan(ay),zhuanhuan(az),
                zhuanhuan(bx),zhuanhuan(by),zhuanhuan(bz));
    }


    public static Double getMinDistance(String ax, String ay, String az,
                                        String bx, String by, String bz){
        return getDistance(ax,ay,az,bx,by,bz);

    }



    private static Double getMath(BigDecimal ax, BigDecimal ay, BigDecimal az,
                                  BigDecimal nx, BigDecimal ny, BigDecimal nz){
        BigDecimal anx = ax.subtract(nx);
        BigDecimal any = ay.subtract(ny);
        BigDecimal anz = az.subtract(nz);

        anx = anx.multiply(anx);
        any = any.multiply(any);
        anz = anz.multiply(anz);

        Double an = Math.sqrt(anx.add(any).add(anz).doubleValue());
        return an;
    }



    private static BigDecimal zhuanhuan(String x){
        return new BigDecimal(x);
    }


    // 计算两点之间的距离
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    // 根据点 A(x1, y1, z1) 和点 B(x2, y2, z2) 获取距离 A 点 n 的新点坐标
    public static double[] getPointAtDistance(double x1, double y1, double z1, double x2, double y2, double z2, double n) {
        // 计算两点之间的距离
        double distance = distance(x1, y1, z1, x2, y2, z2);

        // 计算单位向量
        double unitVectorX = (x2 - x1) / distance;
        double unitVectorY = (y2 - y1) / distance;
        double unitVectorZ = (z2 - z1) / distance;

        // 计算新点的坐标
        double newX = x1 + n * unitVectorX;
        double newY = y1 + n * unitVectorY;
        double newZ = z1 + n * unitVectorZ;

        return new double[]{newX, newY, newZ};
    }

    public static void main(String[] args) {
        // 点 A 和 点 B 的坐标
        double x1 = 1, y1 = 2, z1 = 3; // 点 A
        double x2 = 4, y2 = 6, z2 = 8; // 点 B

        // 距离 A 点的距离 n
        double n = 3;

        // 获取新点坐标
        double[] point = getPointAtDistance(x1, y1, z1, x2, y2, z2, n);
        System.out.println("新点的坐标: (" + point[0] + ", " + point[1] + ", " + point[2] + ")");
    }


}

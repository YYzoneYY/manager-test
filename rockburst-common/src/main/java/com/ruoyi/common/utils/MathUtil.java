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


    /**
     * 将第三个点从笛卡尔坐标系映射到地理坐标系。
     *
     * @param x1 笛卡尔坐标 A 的 x
     * @param y1 笛卡尔坐标 A 的 y
     * @param z1 笛卡尔坐标 A 的 z
     * @param j1 经纬度 A 的经度
     * @param w1 经纬度 A 的纬度
     * @param x2 笛卡尔坐标 B 的 x
     * @param y2 笛卡尔坐标 B 的 y
     * @param z2 笛卡尔坐标 B 的 z
     * @param j2 经纬度 B 的经度
     * @param w2 经纬度 B 的纬度
     * @param x3 第三个点 C 的笛卡尔 x
     * @param y3 第三个点 C 的笛卡尔 y
     * @param z3 第三个点 C 的笛卡尔 z
     * @return 返回第三个点的经纬度 [经度, 纬度]
     */
    public static double[] mapCartesianToGeodetic(
            double x1, double y1, double z1, double j1, double w1,
            double x2, double y2, double z2, double j2, double w2,
            double x3, double y3, double z3
    ) {
        // 计算点 A 和点 B 的笛卡尔距离
        double distanceAB = Math.sqrt(
                Math.pow(x2 - x1, 2) +
                        Math.pow(y2 - y1, 2) +
                        Math.pow(z2 - z1, 2)
        );

        // 计算点 A 和点 B 的经纬度距离
        double deltaLongitude = j2 - j1; // 经度差
        double deltaLatitude = w2 - w1;  // 纬度差

        // 计算笛卡尔单位距离对应的经纬度变化
        double scaleLongitude = deltaLongitude / distanceAB;
        double scaleLatitude = deltaLatitude / distanceAB;

        // 计算点 C 与点 A 的笛卡尔距离
        double distanceAC = Math.sqrt(
                Math.pow(x3 - x1, 2) +
                        Math.pow(y3 - y1, 2) +
                        Math.pow(z3 - z1, 2)
        );

        // 计算点 C 的经纬度
        double longitudeC = j1 + (x3 - x1) * scaleLongitude;
        double latitudeC = w1 + (y3 - y1) * scaleLatitude;

        return new double[]{longitudeC, latitudeC};
    }

    public static void main(String[] args) {
//
//        7796.1399,194.9874,0,36.1977,117.206,
//                7795.8098,7297.6209,0, 36.2491,117.206 ,
    }


}

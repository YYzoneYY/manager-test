package com.ruoyi.system.domain.utils;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.domain.dto.BizPlanPrePointDto;
import com.ruoyi.system.domain.dto.TraversePointGatherDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: shikai
 * @date: 2025/2/13
 * @description:
 */
public class DataJudgeUtils {

    public static boolean greaterThan(String s, String min) {
        boolean flag = false;
        double s1 = Double.parseDouble(s);
        double main1 = Double.parseDouble(min);
        if (s1 >= main1) {
            flag = true;
        }
        return flag;
    }

    public static boolean lessThan(String s, String max) {
        boolean flag = false;
        double s1 = 0.0;
        if (s.charAt(0) == ' ') {
            s1 = Double.parseDouble(s.trim());
        } else {
            s1 = Double.parseDouble(s.substring(1));
        }
        double max1 = Double.parseDouble(max.substring(1));
        if (s1 <= max1) {
            flag = true;
        }
        return flag;
    }

    public static boolean isInRange(String s, String min, String max) {
        boolean flag = false;
        double s1 = Double.parseDouble(s);
        double min1 = Double.parseDouble(min);
        double max1 = Double.parseDouble(max);
        if (min1 <= s1 && s1 <= max1) {
            flag = true;
        }
        return flag;
    }

    public static boolean compare(String s, String min) {
        boolean flag = false;
        double s1 = Double.parseDouble(s);
        double min1 = Double.parseDouble(min);
        if (s1 < min1) {
            flag = true;
        }
        return flag;
    }

    public static boolean compareTwo(String s, String min) {
        boolean flag = false;
        double s1 = Double.parseDouble(s);
        double min1 = Double.parseDouble(min);
        if (s1 <= min1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 两数绝对值比较（>=）
     */
    public static boolean absoluteValueCompare(String s, String min) {
        boolean flag = false;
        double s1 = Double.parseDouble(s);
        double min1 = Double.parseDouble(min);
        if (Math.abs(s1) >= Math.abs(min1)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 两数绝对值比较（>=）
     */
    public static boolean absoluteValueCompareTwo(double s, String min) {
        boolean flag = false;
        double min1 = Double.parseDouble(min);
        if (Math.abs(s) > Math.abs(min1)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 两数差值
     */
    public static double doingPoorly(Double max, Double s1) {
        double num = 0.0;
        double abs = Math.abs(s1);
        num = max - abs;
        return num;
    }
}
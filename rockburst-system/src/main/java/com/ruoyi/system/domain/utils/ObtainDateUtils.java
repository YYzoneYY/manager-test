package com.ruoyi.system.domain.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * @author: shikai
 * @date: 2025/8/15
 * @description:
 */
public class ObtainDateUtils {

    /**
     * 获取前30分钟的时间戳
     * @param currentTime 当前时间戳
     */
    public static Long getThirtyMinutesTime(Long currentTime) {
        long thirtyMinutesAgo = 0L;
        thirtyMinutesAgo = currentTime - 30 * 60 * 1000L;
        return thirtyMinutesAgo;
    }

    /**
     * 获取一小时的时间戳
     * @param currentTime 当前时间戳
     */
    public static Long getOneHourTime(Long currentTime) {
        long oneHourAgo = 0L;
        oneHourAgo = currentTime - 60 * 60 * 1000L;
        return oneHourAgo;
    }

    /**
     * 获取一天的时间戳
     * @param currentTime 当前时间戳
     */
    public static Long getTwentyFourHoursTime(Long currentTime) {
        long twentyFourHoursAgo = 0L;
        twentyFourHoursAgo = currentTime - 24 * 60 * 60 * 1000L;
        return twentyFourHoursAgo;
    }


    public static Long getCurrentZoneTime() {
        long startOfDayMillis = 0L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.with(LocalTime.MIN);
        startOfDayMillis = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return startOfDayMillis;
    }

    public static Long getCurrentTwentyFourHoursTime() {
        long endOfDayMillis = 0L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.with(LocalTime.MAX);
        endOfDayMillis = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return endOfDayMillis;
    }
}
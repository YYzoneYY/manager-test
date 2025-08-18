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
     * 获取后30分钟的时间戳
     * @param currentTime 当前时间戳
     */
    public static Long getThirtyMinutesAfterTime(Long currentTime) {
        long thirtyMinutesAfter = 0L;
        thirtyMinutesAfter = currentTime + 30 * 60 * 1000L;
        return thirtyMinutesAfter;
    }

    /**
     * 判断后30分钟的时间是否超过当前时间
     * @param currentTime 当前时间戳
     * @param targetTime 目标时间戳
     */
    public static boolean isOverThirtyMinutesAfterTime(Long currentTime, Long targetTime) {
        Long thirtyMinutesAfter = getThirtyMinutesAfterTime(targetTime);
        return thirtyMinutesAfter > currentTime;
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
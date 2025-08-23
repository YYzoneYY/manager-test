package com.ruoyi.system.domain.utils;

import java.time.*;

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
        return targetTime > currentTime;
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


    /**
     * 获取指定时间戳所在天的开始时间戳（00:00:00）
     * @param currentTime 指定时间戳
     * @return 当天开始时间戳
     */
    public static Long getCurrentZoneTime(Long currentTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime startOfDay = dateTime.with(LocalTime.MIN);
        return startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 获取指定时间戳所在天的结束时间戳（23:59:59）
     * @param currentTime 指定时间戳
     * @return 当天结束时间戳
     */
    public static Long getCurrentTwentyFourHoursTime(Long currentTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime endOfDay = dateTime.with(LocalTime.MAX);
        return endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取指定时间戳所在周的开始时间戳（周一 00:00:00）
     * @param currentTime 指定时间戳
     * @return 当周开始时间戳
     */
    public static Long getCurrentWeekStartTime(Long currentTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime startOfWeek = dateTime.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        return startOfWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 获取指定时间戳所在周的结束时间戳（周日 23:59:59）
     * @param currentTime 指定时间戳
     * @return 当周结束时间戳
     */
    public static Long getCurrentWeekEndTime(Long currentTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime endOfWeek = dateTime.with(DayOfWeek.SUNDAY).with(LocalTime.MAX);
        return endOfWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取指定时间戳所在月的开始时间戳（1号 00:00:00）
     * @param currentTime 指定时间戳
     * @return 当月开始时间戳
     */
    public static Long getCurrentMonthStartTime(Long currentTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime startOfMonth = dateTime.withDayOfMonth(1).with(LocalTime.MIN);
        return startOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    /**
     * 获取指定时间戳所在月的结束时间戳（月末最后一天 23:59:59）
     * @param currentTime 指定时间戳
     * @return 当月结束时间戳
     */
    public static Long getCurrentMonthEndTime(Long currentTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDateTime endOfMonth = dateTime.withDayOfMonth(dateTime.toLocalDate().lengthOfMonth()).with(LocalTime.MAX);
        return endOfMonth.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
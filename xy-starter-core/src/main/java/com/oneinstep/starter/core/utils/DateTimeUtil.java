package com.oneinstep.starter.core.utils;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 */
@UtilityClass
@Slf4j
public class DateTimeUtil {

    /**
     * 日期格式 yyyyMMdd
     */
    public static final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    /**
     * 日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.systemDefault());

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式里面为LocalDateTime
     */
    public static LocalDateTime convertStringToLocalDateTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return LocalDateTime.parse(timeStr, formatter);
    }

    public static LocalDateTime startOfToday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    public static LocalDateTime endOfToday() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
    }

    /**
     * 获取昨天
     */
    public static LocalDate yesterdayLocalDate() {
        return LocalDate.now().minusDays(1);
    }

    /**
     * 获取当天日期 LocalDate 格式
     */
    public static LocalDate todayLocalDate() {
        return LocalDate.now();
    }


    /**
     * 获取今天 0 点的时间 LocalDateTime 类型
     */
    public static LocalDateTime todayZero() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 返回某个日期的 0 点的时间 LocalDateTime 类型
     */
    public static LocalDateTime zeroOfDate(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * 返回某个日期的 23:59:59 的时间 LocalDateTime 类型
     */
    public static LocalDateTime endOfDate(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }

    /**
     * 获取昨天 0 点的时间 ZonedDateTime 类型
     */
    public static LocalDateTime yesterdayZero() {
        return LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MIN);
    }

    /**
     * 获取明天 0 点的时间 ZonedDateTime 类型
     */
    public static LocalDateTime tomorrowZero() {
        return LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN);
    }

    /**
     * 获取当前时间
     *
     * @return yyyyMMdd
     */
    public static int todayInt() {
        String currentDateStr = ZonedDateTime.now(ZoneId.systemDefault()).format(YYYYMMDD);
        return Integer.parseInt(currentDateStr);
    }

    /**
     * 获取明天时间
     *
     * @return 明天时间 yyyyMMdd
     */
    public static int tomorrow() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        String tomorrowDateStr = now.plusDays(1).format(YYYYMMDD);
        return Integer.parseInt(tomorrowDateStr);
    }

    /**
     * 格式化时间 ZonedDateTime 为 {yyyyMMdd} （int）
     *
     * @return yyyyMMdd
     */
    public static String formatDateTime_YYYY_MM_DD_HH_MM_SS(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(YYYY_MM_DD_HH_MM_SS);
    }

    public static String formatDate_YYYY_MM_DD(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(YYYY_MM_DD);
    }

    public static String formatDateTime_YYYYMMDDHHMMSS(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(YYYYMMDDHHMMSS);
    }

    /**
     * 格式化时间 ZonedDateTime 为 {yyyyMMdd} （int）
     *
     * @return yyyyMMdd
     */
    public static String formatDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return dateTime.format(YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 将字符串转换为 ZonedDateTime，字符串格式需要为 yyyy-MM-dd HH:mm:ss
     *
     * @param timeStr 时间字符串
     * @return ZonedDateTime
     */
    public static ZonedDateTime convertStrToZonedDateTime(@NotNull String timeStr) {
        if (!StringUtils.hasText(timeStr)) {
            return null;
        }
        try {
            return ZonedDateTime.parse(timeStr, YYYY_MM_DD_HH_MM_SS);
        } catch (Exception e) {
            log.error("Convert timeStr to ZonedDateTime error. Maybe the format of timeStr is invalid. Make sure it's yyyy-MM-dd HH:mm:ss.", e);
        }
        return null;
    }

    /**
     * 获取某一天的开始时间和结束时间
     * 返回 pair
     */
    public static Pair<LocalDateTime, LocalDateTime> getStartAndEndTimeOfDay(LocalDate date) {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
        return Pair.of(start, end);
    }

    /**
     * 获取某个日期（int）加上{days}天的日期
     *
     * @return yyyyMMdd
     */
    public static Integer plusDays(Integer date, int days) {
        LocalDate dateTime = LocalDate.parse(String.valueOf(date), YYYYMMDD);
        String tomorrowDateStr = dateTime.plusDays(days).format(YYYYMMDD);
        return Integer.parseInt(tomorrowDateStr);
    }

    /**
     * 将日期（int）转换为 LocalDate
     *
     * @return LocalDate
     */
    public static LocalDate convertDateIntToLocalDate(Integer date) {
        return LocalDate.parse(String.valueOf(date), YYYYMMDD);
    }

    /**
     * 将日期（int）转换为 LocalDate
     *
     * @return LocalDate
     */
    public static Integer convertLocalDateToInt(LocalDate date) {
        return Integer.parseInt(date.format(YYYYMMDD));
    }

    /**
     * 获取某个日期（int）减去{days}天的日期
     *
     * @return yyyyMMdd
     */
    public static Integer minusDays(Integer date, int days) {
        LocalDate dateTime = LocalDate.parse(String.valueOf(date), YYYYMMDD);
        String tomorrowDateStr = dateTime.minusDays(days).format(YYYYMMDD);
        return Integer.parseInt(tomorrowDateStr);
    }

    /**
     * @param localDateTime
     * @return
     */
    public static long convertToTimestamp(LocalDateTime localDateTime) {
        // Convert LocalDateTime to Instant
        Instant instant = localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();
        // Convert Instant to timestamp (milliseconds since Unix epoch)
        return instant.toEpochMilli();
    }


}

package com.salamander.core.utils;

import androidx.annotation.Nullable;

import com.salamander.core.Utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

public class DateUtils {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME_FULL = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME_NO_SEC = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_UI = "EEEE, dd MMMM yyyy";
    public static final String FORMAT_UI_NO_DAY = "dd MMMM yyyy";
    public static final String FORMAT_UI_NO_DAY_SHORT_MONTH = "dd MMM yyyy";
    public static final String FORMAT_TIME_FULL = "HH:mm:ss";
    public static final String FORMAT_TIME_NO_SECOND = "HH:mm";

    private static String toString(String format, long dateTimeMillis) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimeMillis), ZoneId.systemDefault());
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static String toString(long dateTimeMillis) {
        return toString(FORMAT_DATETIME_FULL, dateTimeMillis);
    }

    public static String format(String format, long timeMillis) {
        return toString(format, timeMillis);
    }

    public static long toLong(String format, @Nullable String dateTimeStr) {
        if (Utils.isEmpty(dateTimeStr))
            return 0;
        else if (dateTimeStr.contains(":")) {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(format));
            return localDateTime.toInstant(ZoneId.systemDefault().getRules().getOffset(localDateTime)).toEpochMilli();
        } else {
            try {
                LocalDate localDate = LocalDate.parse(dateTimeStr, DateTimeFormatter.ofPattern(format));
                LocalDateTime localDateTime = localDate.atStartOfDay();
                return localDateTime.toInstant(ZoneId.systemDefault().getRules().getOffset(localDateTime)).toEpochMilli();
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public static LocalDate toLocalDate(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(long millis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    public static long toLong(String dateTimeStr) {
        return toLong(FORMAT_DATETIME_FULL, dateTimeStr);
    }

    public static long toLong(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneId.systemDefault().getRules().getOffset(localDateTime)).toEpochMilli();
    }

    public static long toLong(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.toInstant(ZoneId.systemDefault().getRules().getOffset(localDateTime)).toEpochMilli();
    }

    public static int interval(ChronoUnit chronoUnit, long fromDate, long toDate) {
        return (int) chronoUnit.between(toLocalDate(toDate), toLocalDate(fromDate));
    }
}
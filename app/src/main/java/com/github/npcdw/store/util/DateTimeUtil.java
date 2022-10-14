package com.github.npcdw.store.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    private static final String DATETIME_TIMEZONE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX";
    private static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final String DATE_FORMAT_WITHOUT_SPLIT_PATTERN = "yyyyMMdd";
    private static final String TIME_FORMAT_PATTERN = "HH:mm:ss";

    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
        return sdf.format(new Date());
    }

    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_PATTERN);
        return sdf.format(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        return sdf.format(date);
    }

    public static Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        return sdf.parse(date);
    }

    public static String formatDateWithoutSplit(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_WITHOUT_SPLIT_PATTERN);
        return sdf.format(date);
    }

    public static String formatDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
        return sdf.format(date);
    }

    public static Date parseDateTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_PATTERN);
        return sdf.parse(date);
    }

    public static String formatDateTimeTimezone(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_TIMEZONE_FORMAT_PATTERN);
        return sdf.format(date);
    }

    public static Date parseDateTimeTimezone(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_TIMEZONE_FORMAT_PATTERN);
        return sdf.parse(date);
    }

}

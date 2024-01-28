package com.github.npcdw.store.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtil {
    private const val DATETIME_TIMEZONE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssX"
    private const val DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT_PATTERN = "yyyy-MM-dd"
    private const val DATE_FORMAT_WITHOUT_SPLIT_PATTERN = "yyyyMMdd"
    private const val TIME_FORMAT_PATTERN = "HH:mm:ss"
    val dateTime: String
        get() {
            val dtf = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)
            return dtf.format(LocalDateTime.now())
        }

    fun formatTime(date: LocalTime): String {
        val dtf = DateTimeFormatter.ofPattern(TIME_FORMAT_PATTERN)
        return dtf.format(date)
    }

    fun formatDate(date: LocalDate): String {
        val dtf = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)
        return dtf.format(date)
    }

    fun parseDate(date: String): LocalDate {
        val dtf = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)
        return LocalDate.parse(date, dtf);
    }

    fun formatDateWithoutSplit(date: LocalDateTime): String {
        val dtf = DateTimeFormatter.ofPattern(DATE_FORMAT_WITHOUT_SPLIT_PATTERN)
        return dtf.format(date)
    }

    fun formatDateTime(date: LocalDateTime): String {
        val dtf = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)
        return dtf.format(date)
    }

    fun parseDateTime(date: String): LocalDateTime {
        val dtf = DateTimeFormatter.ofPattern(DATETIME_FORMAT_PATTERN)
        return LocalDateTime.parse(date, dtf);
    }

    fun formatDateTimeTimezone(date: ZonedDateTime): String {
        val dtf = DateTimeFormatter.ofPattern(DATETIME_TIMEZONE_FORMAT_PATTERN)
        return dtf.format(date)
    }

    fun parseDateTimeTimezone(date: String): ZonedDateTime {
        val dtf = DateTimeFormatter.ofPattern(DATETIME_TIMEZONE_FORMAT_PATTERN)
        return ZonedDateTime.parse(date, dtf);
    }
}

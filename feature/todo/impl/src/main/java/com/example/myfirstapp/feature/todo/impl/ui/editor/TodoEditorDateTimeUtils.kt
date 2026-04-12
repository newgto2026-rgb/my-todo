package com.example.myfirstapp.feature.todo.impl.ui

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal val REMINDER_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

internal fun reminderDateTimeToEpochMillis(value: String): Long? {
    if (value.isBlank()) return null
    return runCatching {
        LocalDateTime.parse(value.trim(), REMINDER_FORMATTER)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}

internal fun epochMillisToReminderDateTime(value: Long?): String {
    if (value == null) return ""
    return runCatching {
        Instant.ofEpochMilli(value)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(REMINDER_FORMATTER)
    }.getOrDefault("")
}

internal fun parseReminderInput(value: String): LocalDateTime? {
    if (value.isBlank()) return null
    return runCatching { LocalDateTime.parse(value, REMINDER_FORMATTER) }.getOrNull()
}

internal fun utcMillisToIsoDate(millis: Long?): String {
    if (millis == null) return ""
    return runCatching {
        Instant.ofEpochMilli(millis)
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
    }.getOrDefault("")
}

internal fun isoDateToUtcMillis(value: String): Long? {
    if (value.isBlank()) return null
    return runCatching {
        LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }.getOrNull()
}

package com.example.myfirstapp.core.data.mapper

import com.example.myfirstapp.core.database.entity.ReminderEntity
import com.example.myfirstapp.core.model.Reminder
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.ReminderStatus

fun ReminderEntity.toDomain(): Reminder =
    Reminder(
        id = id,
        title = title,
        note = note,
        triggerAtEpochMillis = triggerAtEpochMillis,
        repeatType = ReminderRepeatType.valueOf(repeatType),
        repeatDaysMask = repeatDaysMask,
        isEnabled = isEnabled,
        status = ReminderStatus.valueOf(status),
        lastTriggeredAtEpochMillis = lastTriggeredAtEpochMillis,
        createdAtEpochMillis = createdAt,
        updatedAtEpochMillis = updatedAt
    )

fun Reminder.toEntity(): ReminderEntity =
    ReminderEntity(
        id = id,
        title = title,
        note = note,
        triggerAtEpochMillis = triggerAtEpochMillis,
        repeatType = repeatType.name,
        repeatDaysMask = repeatDaysMask,
        isEnabled = isEnabled,
        status = status.name,
        lastTriggeredAtEpochMillis = lastTriggeredAtEpochMillis,
        createdAt = createdAtEpochMillis,
        updatedAt = updatedAtEpochMillis
    )

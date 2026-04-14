package com.example.myfirstapp.core.data.mapper

import com.example.myfirstapp.core.database.entity.TodoEntity
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoItem
import java.time.LocalDate

fun TodoEntity.toDomain(): TodoItem =
    TodoItem(
        id = id,
        title = title,
        isDone = isDone,
        dueDate = dueDateEpochDay?.let(LocalDate::ofEpochDay),
        dueTimeMinutes = dueTimeMinutes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        categoryId = categoryId,
        reminderAtEpochMillis = reminderAtEpochMillis,
        isReminderEnabled = isReminderEnabled,
        reminderRepeatType = ReminderRepeatType.valueOf(reminderRepeatType),
        reminderRepeatDaysMask = reminderRepeatDaysMask,
        reminderLeadMinutes = reminderLeadMinutes
    )

fun TodoItem.toEntity(): TodoEntity =
    TodoEntity(
        id = id,
        title = title,
        isDone = isDone,
        dueDateEpochDay = dueDate?.toEpochDay(),
        dueTimeMinutes = dueTimeMinutes,
        reminderAtEpochMillis = reminderAtEpochMillis,
        isReminderEnabled = isReminderEnabled,
        reminderRepeatType = reminderRepeatType.name,
        reminderRepeatDaysMask = reminderRepeatDaysMask,
        reminderLeadMinutes = reminderLeadMinutes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        categoryId = categoryId
    )

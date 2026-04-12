package com.example.myfirstapp.feature.todo.impl.model

import com.example.myfirstapp.core.model.ReminderRepeatType
import java.time.LocalDate

data class TodoEditModel(
    val id: Long?,
    val title: String,
    val dueDate: LocalDate?,
    val categoryId: Long?,
    val reminderAtEpochMillis: Long?,
    val isReminderEnabled: Boolean,
    val reminderRepeatType: ReminderRepeatType
)

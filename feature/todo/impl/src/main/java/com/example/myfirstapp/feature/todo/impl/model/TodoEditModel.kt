package com.example.myfirstapp.feature.todo.impl.model

import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoPriority
import java.time.LocalDate

data class TodoEditModel(
    val id: Long?,
    val title: String,
    val dueDate: LocalDate?,
    val dueTimeMinutes: Int?,
    val priority: TodoPriority,
    val reminderAtEpochMillis: Long?,
    val isReminderEnabled: Boolean,
    val reminderRepeatType: ReminderRepeatType,
    val reminderLeadMinutes: Int?
)

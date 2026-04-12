package com.example.myfirstapp.feature.todo.impl.model

import androidx.compose.runtime.Immutable
import com.example.myfirstapp.core.model.ReminderRepeatType

@Immutable
data class TodoItemUiModel(
    val id: Long,
    val title: String,
    val isDone: Boolean,
    val dueDateText: String?,
    val reminderDateTimeText: String?,
    val isReminderEnabled: Boolean,
    val reminderRepeatType: ReminderRepeatType,
    val categoryId: Long?,
    val categoryName: String?,
    val categoryColorHex: String?
)

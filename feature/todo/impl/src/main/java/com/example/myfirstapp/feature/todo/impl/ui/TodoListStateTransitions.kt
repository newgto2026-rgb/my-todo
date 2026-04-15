package com.example.myfirstapp.feature.todo.impl.ui

import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoPriority
import com.example.myfirstapp.feature.todo.impl.model.TodoEditModel
import com.example.myfirstapp.feature.todo.impl.model.TodoItemUiModel

internal fun TodoListUiState.openNewTodoEditor(): TodoListUiState = copy(
    isEditDialogVisible = true,
    editingItem = null,
    draftTitle = "",
    draftDueDateInput = "",
    draftDueTimeInput = "",
    draftReminderEnabled = false,
    draftReminderLeadMinutes = DEFAULT_REMINDER_LEAD_MINUTES,
    draftReminderRepeatType = ReminderRepeatType.NONE,
    draftPriority = TodoPriority.MEDIUM,
    errorMessageRes = null
)

internal fun TodoListUiState.dismissTodoEditor(): TodoListUiState = copy(
    isEditDialogVisible = false,
    editingItem = null,
    draftTitle = "",
    draftDueDateInput = "",
    draftDueTimeInput = "",
    draftReminderEnabled = false,
    draftReminderLeadMinutes = DEFAULT_REMINDER_LEAD_MINUTES,
    draftReminderRepeatType = ReminderRepeatType.NONE,
    draftPriority = TodoPriority.MEDIUM,
    errorMessageRes = null
)

internal fun TodoItemUiModel.toTodoEditModel(): TodoEditModel =
    TodoEditModel(
        id = id,
        title = title,
        dueDate = parseIsoDateInput(dueDateText.orEmpty()),
        dueTimeMinutes = dueTimeTextToMinutes(dueTimeText),
        priority = priority,
        reminderAtEpochMillis = reminderAtEpochMillis,
        isReminderEnabled = isReminderEnabled,
        reminderRepeatType = reminderRepeatType.normalizeRepeatType(),
        reminderLeadMinutes = reminderLeadMinutes
    )

internal fun ReminderRepeatType.normalizeRepeatType(): ReminderRepeatType =
    if (this == ReminderRepeatType.CUSTOM_DAYS) ReminderRepeatType.NONE else this

internal const val DEFAULT_REMINDER_LEAD_MINUTES = 10

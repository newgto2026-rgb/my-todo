package com.example.myfirstapp.feature.todo.impl.ui

import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
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
    draftCategoryId = null,
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
    draftCategoryId = null,
    errorMessageRes = null
)

internal fun TodoListUiState.openCategoryManager(): TodoListUiState = copy(
    isCategoryManagerVisible = true,
    editingCategoryId = null,
    categoryNameInput = "",
    categoryColorInput = "",
    categoryIconInput = ""
)

internal fun TodoListUiState.dismissCategoryManager(): TodoListUiState = copy(
    isCategoryManagerVisible = false,
    editingCategoryId = null,
    categoryNameInput = "",
    categoryColorInput = "",
    categoryIconInput = ""
)

internal fun TodoListUiState.editCategory(category: CategoryUiModel): TodoListUiState = copy(
    isCategoryManagerVisible = true,
    editingCategoryId = category.id,
    categoryNameInput = category.name,
    categoryColorInput = category.colorHex.orEmpty(),
    categoryIconInput = ""
)

internal fun TodoListUiState.clearCategoryEditor(): TodoListUiState = copy(
    editingCategoryId = null,
    categoryNameInput = "",
    categoryColorInput = "",
    categoryIconInput = ""
)

internal fun TodoItemUiModel.toTodoEditModel(): TodoEditModel =
    TodoEditModel(
        id = id,
        title = title,
        dueDate = parseIsoDateInput(dueDateText.orEmpty()),
        dueTimeMinutes = dueTimeTextToMinutes(dueTimeText),
        categoryId = categoryId,
        reminderAtEpochMillis = reminderAtEpochMillis,
        isReminderEnabled = isReminderEnabled,
        reminderRepeatType = reminderRepeatType.normalizeRepeatType(),
        reminderLeadMinutes = reminderLeadMinutes
    )

internal fun ReminderRepeatType.normalizeRepeatType(): ReminderRepeatType =
    if (this == ReminderRepeatType.CUSTOM_DAYS) ReminderRepeatType.NONE else this

internal const val DEFAULT_REMINDER_LEAD_MINUTES = 10

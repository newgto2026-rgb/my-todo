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
    draftReminderEnabled = false,
    draftReminderDateTimeInput = "",
    draftReminderRepeatType = ReminderRepeatType.NONE,
    draftCategoryId = null,
    errorMessageRes = null
)

internal fun TodoListUiState.dismissTodoEditor(): TodoListUiState = copy(
    isEditDialogVisible = false,
    editingItem = null,
    draftTitle = "",
    draftDueDateInput = "",
    draftReminderEnabled = false,
    draftReminderDateTimeInput = "",
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
        categoryId = categoryId,
        reminderAtEpochMillis = reminderDateTimeToEpochMillis(reminderDateTimeText.orEmpty()),
        isReminderEnabled = isReminderEnabled,
        reminderRepeatType = reminderRepeatType.normalizeRepeatType()
    )

internal fun ReminderRepeatType.normalizeRepeatType(): ReminderRepeatType =
    if (this == ReminderRepeatType.CUSTOM_DAYS) ReminderRepeatType.NONE else this

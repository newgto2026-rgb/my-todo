package com.example.myfirstapp.feature.todo.impl.ui

import com.example.myfirstapp.core.model.Category
import com.example.myfirstapp.core.model.TodoCategoryFilter
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.model.TodoItem
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
import com.example.myfirstapp.feature.todo.impl.model.TodoItemUiModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun buildTodoListUiState(
    localState: TodoListUiState,
    items: List<TodoItem>,
    selectedFilter: TodoFilter,
    categories: List<Category>,
    selectedCategoryId: Long?
): TodoListUiState {
    val normalizedSelectedCategoryId = categories.normalizeSelectedCategoryId(selectedCategoryId)
    val categoriesById = categories.associateBy { it.id }
    val filteredItems = items
        .filterBy(selectedFilter)
        .filterByCategory(normalizedSelectedCategoryId)

    return localState.copy(
        items = filteredItems.map { it.toUiModel(categoriesById) },
        categories = categories.map { it.toUiModel() },
        selectedFilter = selectedFilter,
        selectedCategoryId = normalizedSelectedCategoryId,
        isLoading = false
    )
}

private fun List<Category>.normalizeSelectedCategoryId(selectedCategoryId: Long?): Long? {
    val existingIds = mapTo(mutableSetOf()) { it.id }
    return if (
        selectedCategoryId == null ||
        selectedCategoryId in existingIds ||
        selectedCategoryId == TodoCategoryFilter.UNCATEGORIZED_FILTER_ID
    ) {
        selectedCategoryId
    } else {
        null
    }
}

private fun List<TodoItem>.filterBy(filter: TodoFilter): List<TodoItem> {
    val today = LocalDate.now()
    return when (filter) {
        TodoFilter.ALL -> this
        TodoFilter.TODAY -> filter { it.dueDate == today }
        TodoFilter.COMPLETED -> filter { it.isDone }
    }
}

private fun List<TodoItem>.filterByCategory(categoryId: Long?): List<TodoItem> {
    if (categoryId == null) return this
    if (categoryId == TodoCategoryFilter.UNCATEGORIZED_FILTER_ID) {
        return filter { it.categoryId == null }
    }
    return filter { it.categoryId == categoryId }
}

private fun Category.toUiModel(): CategoryUiModel =
    CategoryUiModel(
        id = id,
        name = name,
        colorHex = colorHex
    )

private fun TodoItem.toUiModel(categoriesById: Map<Long, Category>): TodoItemUiModel {
    val category = categoryId?.let(categoriesById::get)
    return TodoItemUiModel(
        id = id,
        title = title,
        isDone = isDone,
        dueDateText = dueDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
        reminderDateTimeText = epochMillisToReminderDateTime(reminderAtEpochMillis),
        isReminderEnabled = isReminderEnabled,
        reminderRepeatType = reminderRepeatType,
        categoryId = categoryId,
        categoryName = category?.name ?: UNCATEGORIZED_LABEL,
        categoryColorHex = category?.colorHex
    )
}

private const val UNCATEGORIZED_LABEL = "Uncategorized"

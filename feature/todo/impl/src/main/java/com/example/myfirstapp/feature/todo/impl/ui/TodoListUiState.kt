package com.example.myfirstapp.feature.todo.impl.ui

import androidx.compose.runtime.Immutable
import androidx.annotation.StringRes
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
import com.example.myfirstapp.feature.todo.impl.model.TodoEditModel
import com.example.myfirstapp.feature.todo.impl.model.TodoItemUiModel

@Immutable
data class TodoListUiState(
    val items: List<TodoItemUiModel> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),
    val selectedFilter: TodoFilter = TodoFilter.ALL,
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = false,
    val isEditDialogVisible: Boolean = false,
    val isCategoryManagerVisible: Boolean = false,
    val editingItem: TodoEditModel? = null,
    val draftCategoryId: Long? = null,
    val editingCategoryId: Long? = null,
    val categoryNameInput: String = "",
    val categoryColorInput: String = "",
    val categoryIconInput: String = "",
    val draftTitle: String = "",
    val draftDueDateInput: String = "",
    val draftDueTimeInput: String = "",
    val draftReminderEnabled: Boolean = false,
    val draftReminderLeadMinutes: Int? = null,
    val draftReminderRepeatType: ReminderRepeatType = ReminderRepeatType.NONE,
    @StringRes val errorMessageRes: Int? = null
)

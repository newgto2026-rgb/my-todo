package com.example.myfirstapp.feature.todo.impl.ui

import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoFilter

sealed interface TodoListAction {
    data object OnAddClick : TodoListAction
    data class OnTitleChange(val value: String) : TodoListAction
    data class OnDueDateInputChange(val value: String) : TodoListAction
    data class OnDueTimeInputChange(val value: String) : TodoListAction
    data class OnReminderEnabledChange(val value: Boolean) : TodoListAction
    data class OnReminderLeadMinutesChange(val value: Int) : TodoListAction
    data class OnReminderRepeatTypeChange(val value: ReminderRepeatType) : TodoListAction
    data class OnCategorySelectedInEditor(val categoryId: Long?) : TodoListAction
    data object OnSaveClick : TodoListAction
    data class OnToggleDone(val id: Long) : TodoListAction
    data class OnEditClick(val id: Long) : TodoListAction
    data class OnDeleteClick(val id: Long) : TodoListAction
    data class OnFilterChange(val filter: TodoFilter) : TodoListAction
    data class OnCategoryFilterChange(val categoryId: Long?) : TodoListAction
    data object OnManageCategoriesClick : TodoListAction
    data object OnDismissCategoryManager : TodoListAction
    data class OnCategoryNameInputChange(val value: String) : TodoListAction
    data class OnCategoryColorInputChange(val value: String) : TodoListAction
    data class OnCategoryIconInputChange(val value: String) : TodoListAction
    data class OnCategoryEditClick(val categoryId: Long) : TodoListAction
    data object OnCategorySaveClick : TodoListAction
    data class OnCategoryDeleteClick(val categoryId: Long) : TodoListAction
    data object OnDismissDialog : TodoListAction
}

package com.example.myfirstapp.feature.todo.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.core.domain.scheduler.TodoReminderScheduler
import com.example.myfirstapp.core.domain.usecase.AddCategoryUseCase
import com.example.myfirstapp.core.domain.usecase.AddTodoUseCase
import com.example.myfirstapp.core.domain.usecase.DeleteCategoryUseCase
import com.example.myfirstapp.core.domain.usecase.DeleteTodoUseCase
import com.example.myfirstapp.core.domain.usecase.GetTodoUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveCategoriesUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveSelectedCategoryFilterUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveSelectedTodoFilterUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveTodosUseCase
import com.example.myfirstapp.core.domain.usecase.ToggleTodoDoneUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateCategoryUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateSelectedCategoryFilterUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateSelectedTodoFilterUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateTodoUseCase
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.feature.todo.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class TodoListViewModel @Inject constructor(
    observeTodosUseCase: ObserveTodosUseCase,
    observeSelectedTodoFilterUseCase: ObserveSelectedTodoFilterUseCase,
    observeCategoriesUseCase: ObserveCategoriesUseCase,
    observeSelectedCategoryFilterUseCase: ObserveSelectedCategoryFilterUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val toggleTodoDoneUseCase: ToggleTodoDoneUseCase,
    private val updateSelectedTodoFilterUseCase: UpdateSelectedTodoFilterUseCase,
    private val updateSelectedCategoryFilterUseCase: UpdateSelectedCategoryFilterUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getTodoUseCase: GetTodoUseCase,
    private val todoReminderScheduler: TodoReminderScheduler
) : ViewModel() {

    private val uiLocalState = MutableStateFlow(TodoListUiState(isLoading = true))
    private val sideEffectMutable = MutableSharedFlow<TodoListSideEffect>()

    val sideEffect = sideEffectMutable.asSharedFlow()

    val uiState: StateFlow<TodoListUiState> = combine(
        observeTodosUseCase(),
        observeSelectedTodoFilterUseCase(),
        observeCategoriesUseCase(),
        observeSelectedCategoryFilterUseCase(),
        uiLocalState
    ) { items, selectedFilter, categories, selectedCategoryId, localState ->
        buildTodoListUiState(
            localState = localState,
            items = items,
            selectedFilter = selectedFilter,
            categories = categories,
            selectedCategoryId = selectedCategoryId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TodoListUiState(isLoading = true)
    )

    fun onAction(action: TodoListAction) {
        when (action) {
            TodoListAction.OnAddClick -> updateLocalState { openNewTodoEditor() }

            is TodoListAction.OnTitleChange -> {
                updateLocalState { copy(draftTitle = action.value) }
            }

            is TodoListAction.OnDueDateInputChange -> {
                updateLocalState { copy(draftDueDateInput = action.value) }
            }

            is TodoListAction.OnDueTimeInputChange -> {
                updateLocalState { copy(draftDueTimeInput = action.value) }
            }

            is TodoListAction.OnReminderEnabledChange -> {
                updateLocalState { copy(draftReminderEnabled = action.value) }
            }

            is TodoListAction.OnReminderLeadMinutesChange -> {
                updateLocalState { copy(draftReminderLeadMinutes = action.value) }
            }

            is TodoListAction.OnReminderRepeatTypeChange -> {
                updateLocalState { copy(draftReminderRepeatType = action.value.normalizeRepeatType()) }
            }

            is TodoListAction.OnCategorySelectedInEditor -> {
                updateLocalState { copy(draftCategoryId = action.categoryId) }
            }

            TodoListAction.OnSaveClick -> saveTodo()
            is TodoListAction.OnToggleDone -> toggleDone(action.id)
            is TodoListAction.OnEditClick -> openEditDialog(action.id)
            is TodoListAction.OnDeleteClick -> deleteTodo(action.id)
            is TodoListAction.OnFilterChange -> updateFilter(action.filter)
            is TodoListAction.OnCategoryFilterChange -> updateCategoryFilter(action.categoryId)
            TodoListAction.OnManageCategoriesClick -> updateLocalState { openCategoryManager() }

            TodoListAction.OnDismissCategoryManager -> updateLocalState { dismissCategoryManager() }

            is TodoListAction.OnCategoryNameInputChange -> {
                updateLocalState { copy(categoryNameInput = action.value) }
            }

            is TodoListAction.OnCategoryColorInputChange -> {
                updateLocalState { copy(categoryColorInput = action.value) }
            }

            is TodoListAction.OnCategoryIconInputChange -> {
                updateLocalState { copy(categoryIconInput = action.value) }
            }

            is TodoListAction.OnCategoryEditClick -> {
                val target = uiState.value.categories.firstOrNull { it.id == action.categoryId } ?: return
                updateLocalState { editCategory(target) }
            }

            TodoListAction.OnCategorySaveClick -> saveCategory()
            is TodoListAction.OnCategoryDeleteClick -> deleteCategory(action.categoryId)
            TodoListAction.OnDismissDialog -> updateLocalState { dismissTodoEditor() }
        }
    }

    private fun saveTodo() {
        val current = uiLocalState.value
        val validation = validateTodoDraft(current)
        if (validation.errorMessageRes != null) {
            uiLocalState.value = current.copy(errorMessageRes = validation.errorMessageRes)
            return
        }

        viewModelScope.launch {
            val result: Result<Long> = if (current.editingItem?.id != null) {
                updateTodoUseCase(
                    id = current.editingItem.id,
                    title = checkNotNull(validation.normalizedTitle),
                    dueDate = validation.parsedDueDate,
                    categoryId = current.draftCategoryId,
                    dueTimeMinutes = validation.parsedDueTimeMinutes,
                    reminderAtEpochMillis = if (current.draftReminderEnabled) validation.reminderAtEpochMillis else null,
                    isReminderEnabled = current.draftReminderEnabled,
                    reminderRepeatType = ReminderRepeatType.NONE,
                    reminderRepeatDaysMask = 0,
                    reminderLeadMinutes = if (current.draftReminderEnabled) current.draftReminderLeadMinutes else null
                ).map { current.editingItem.id }
            } else {
                addTodoUseCase(
                    title = checkNotNull(validation.normalizedTitle),
                    dueDate = validation.parsedDueDate,
                    categoryId = current.draftCategoryId,
                    dueTimeMinutes = validation.parsedDueTimeMinutes,
                    reminderAtEpochMillis = if (current.draftReminderEnabled) validation.reminderAtEpochMillis else null,
                    isReminderEnabled = current.draftReminderEnabled,
                    reminderRepeatType = ReminderRepeatType.NONE,
                    reminderRepeatDaysMask = 0,
                    reminderLeadMinutes = if (current.draftReminderEnabled) current.draftReminderLeadMinutes else null
                )
            }

            if (result.isSuccess) {
                result.getOrNull()?.let { syncTodoReminder(it) }
                uiLocalState.value = current.dismissTodoEditor()
            } else {
                sideEffectMutable.emit(TodoListSideEffect.ShowSnackbar(R.string.todo_error_save_failed))
            }
        }
    }

    private fun saveCategory() {
        val current = uiLocalState.value
        val name = current.categoryNameInput.trim()
        if (name.isBlank()) {
            viewModelScope.launch {
                sideEffectMutable.emit(
                    TodoListSideEffect.ShowSnackbar(R.string.todo_error_category_name_required)
                )
            }
            return
        }

        val color = current.categoryColorInput.trim().ifBlank { null }
        val icon = current.categoryIconInput.trim().ifBlank { null }

        viewModelScope.launch {
            val result = if (current.editingCategoryId != null) {
                updateCategoryUseCase(current.editingCategoryId, name, color, icon)
            } else {
                addCategoryUseCase(name, color, icon).map { Unit }
            }

            if (result.isSuccess) {
                uiLocalState.value = current.clearCategoryEditor()
            } else {
                sideEffectMutable.emit(
                    TodoListSideEffect.ShowSnackbar(R.string.todo_error_category_save_failed)
                )
            }
        }
    }

    private fun toggleDone(id: Long) {
        viewModelScope.launch {
            toggleTodoDoneUseCase(id)
                .onFailure {
                    sideEffectMutable.emit(
                        TodoListSideEffect.ShowSnackbar(R.string.todo_error_toggle_done_failed)
                    )
                }
        }
    }

    private fun openEditDialog(id: Long) {
        val target = uiState.value.items.firstOrNull { it.id == id } ?: return
        uiLocalState.value = uiLocalState.value.copy(
            isEditDialogVisible = true,
            editingItem = target.toTodoEditModel(),
            draftTitle = target.title,
            draftDueDateInput = target.dueDateText.orEmpty(),
            draftDueTimeInput = target.dueTimeText.orEmpty(),
            draftReminderEnabled = target.isReminderEnabled,
            draftReminderLeadMinutes = target.reminderLeadMinutes ?: DEFAULT_REMINDER_LEAD_MINUTES,
            draftReminderRepeatType = ReminderRepeatType.NONE,
            draftCategoryId = target.categoryId,
            errorMessageRes = null
        )
    }

    private fun deleteTodo(id: Long) {
        viewModelScope.launch {
            deleteTodoUseCase(id)
                .onSuccess { todoReminderScheduler.cancel(id) }
                .onFailure {
                    sideEffectMutable.emit(
                        TodoListSideEffect.ShowSnackbar(R.string.todo_error_delete_failed)
                    )
                }
        }
    }

    private fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            deleteCategoryUseCase(categoryId)
                .onFailure {
                    sideEffectMutable.emit(
                        TodoListSideEffect.ShowSnackbar(R.string.todo_error_category_delete_failed)
                    )
                }
        }
    }

    private fun updateFilter(filter: TodoFilter) {
        viewModelScope.launch {
            updateSelectedTodoFilterUseCase(filter)
                .onFailure {
                    sideEffectMutable.emit(
                        TodoListSideEffect.ShowSnackbar(R.string.todo_error_filter_change_failed)
                    )
                }
        }
    }

    private fun updateCategoryFilter(categoryId: Long?) {
        viewModelScope.launch {
            updateSelectedCategoryFilterUseCase(categoryId)
                .onFailure {
                    sideEffectMutable.emit(
                        TodoListSideEffect.ShowSnackbar(
                            R.string.todo_error_category_filter_change_failed
                        )
                    )
                }
        }
    }

    private suspend fun syncTodoReminder(todoId: Long) {
        val todo = getTodoUseCase(todoId)
        if (todo != null && todo.isReminderEnabled && todo.reminderAtEpochMillis != null) {
            todoReminderScheduler.schedule(todo)
        } else {
            todoReminderScheduler.cancel(todoId)
        }
    }

    private inline fun updateLocalState(block: TodoListUiState.() -> TodoListUiState) {
        uiLocalState.value = uiLocalState.value.block()
    }
}

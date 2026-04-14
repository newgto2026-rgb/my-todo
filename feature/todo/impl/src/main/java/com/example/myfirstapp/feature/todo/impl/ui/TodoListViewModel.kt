package com.example.myfirstapp.feature.todo.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.core.domain.scheduler.TodoReminderScheduler
import com.example.myfirstapp.core.domain.usecase.AddTodoUseCase
import com.example.myfirstapp.core.domain.usecase.DeleteTodoUseCase
import com.example.myfirstapp.core.domain.usecase.GetTodoUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveSelectedTodoFilterUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveSelectedTodoPriorityFilterUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveTodosUseCase
import com.example.myfirstapp.core.domain.usecase.ToggleTodoDoneUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateSelectedTodoFilterUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateSelectedTodoPriorityFilterUseCase
import com.example.myfirstapp.core.domain.usecase.UpdateTodoUseCase
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.model.TodoPriorityFilter
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
    observeSelectedTodoPriorityFilterUseCase: ObserveSelectedTodoPriorityFilterUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val toggleTodoDoneUseCase: ToggleTodoDoneUseCase,
    private val updateSelectedTodoFilterUseCase: UpdateSelectedTodoFilterUseCase,
    private val updateSelectedTodoPriorityFilterUseCase: UpdateSelectedTodoPriorityFilterUseCase,
    private val getTodoUseCase: GetTodoUseCase,
    private val todoReminderScheduler: TodoReminderScheduler
) : ViewModel() {

    private val uiLocalState = MutableStateFlow(TodoListUiState(isLoading = true))
    private val sideEffectMutable = MutableSharedFlow<TodoListSideEffect>()

    val sideEffect = sideEffectMutable.asSharedFlow()

    val uiState: StateFlow<TodoListUiState> = combine(
        observeTodosUseCase(),
        observeSelectedTodoFilterUseCase(),
        observeSelectedTodoPriorityFilterUseCase(),
        uiLocalState
    ) { items, selectedFilter, selectedPriorityFilter, localState ->
        buildTodoListUiState(
            localState = localState,
            items = items,
            selectedFilter = selectedFilter,
            selectedPriorityFilter = selectedPriorityFilter
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

            is TodoListAction.OnPrioritySelectedInEditor -> {
                updateLocalState { copy(draftPriority = action.priority) }
            }

            TodoListAction.OnSaveClick -> saveTodo()
            is TodoListAction.OnToggleDone -> toggleDone(action.id)
            is TodoListAction.OnEditClick -> openEditDialog(action.id)
            is TodoListAction.OnDeleteClick -> deleteTodo(action.id)
            is TodoListAction.OnFilterChange -> updateFilter(action.filter)
            is TodoListAction.OnPriorityFilterChange -> updatePriorityFilter(action.filter)
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
                    categoryId = null,
                    dueTimeMinutes = validation.parsedDueTimeMinutes,
                    reminderAtEpochMillis = if (current.draftReminderEnabled) validation.reminderAtEpochMillis else null,
                    isReminderEnabled = current.draftReminderEnabled,
                    reminderRepeatType = ReminderRepeatType.NONE,
                    reminderRepeatDaysMask = 0,
                    reminderLeadMinutes = if (current.draftReminderEnabled) current.draftReminderLeadMinutes else null,
                    priority = current.draftPriority
                ).map { current.editingItem.id }
            } else {
                addTodoUseCase(
                    title = checkNotNull(validation.normalizedTitle),
                    dueDate = validation.parsedDueDate,
                    categoryId = null,
                    dueTimeMinutes = validation.parsedDueTimeMinutes,
                    reminderAtEpochMillis = if (current.draftReminderEnabled) validation.reminderAtEpochMillis else null,
                    isReminderEnabled = current.draftReminderEnabled,
                    reminderRepeatType = ReminderRepeatType.NONE,
                    reminderRepeatDaysMask = 0,
                    reminderLeadMinutes = if (current.draftReminderEnabled) current.draftReminderLeadMinutes else null,
                    priority = current.draftPriority
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
            draftPriority = target.priority,
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

    private fun updatePriorityFilter(filter: TodoPriorityFilter) {
        viewModelScope.launch {
            updateSelectedTodoPriorityFilterUseCase(filter)
                .onFailure {
                    sideEffectMutable.emit(
                        TodoListSideEffect.ShowSnackbar(
                            R.string.todo_error_priority_filter_change_failed
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

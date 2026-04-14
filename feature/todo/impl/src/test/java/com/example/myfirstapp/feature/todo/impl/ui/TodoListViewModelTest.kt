package com.example.myfirstapp.feature.todo.impl.ui

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
import com.example.myfirstapp.core.model.TodoPriority
import com.example.myfirstapp.core.model.TodoPriorityFilter
import com.example.myfirstapp.core.testing.repository.FakeTodoRepository
import com.example.myfirstapp.core.testing.rule.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeTodoRepository
    private lateinit var viewModel: TodoListViewModel

    @Before
    fun setUp() {
        repository = FakeTodoRepository()
        viewModel = TodoListViewModel(
            observeTodosUseCase = ObserveTodosUseCase(repository),
            observeSelectedTodoFilterUseCase = ObserveSelectedTodoFilterUseCase(repository),
            observeSelectedTodoPriorityFilterUseCase = ObserveSelectedTodoPriorityFilterUseCase(repository),
            addTodoUseCase = AddTodoUseCase(repository),
            updateTodoUseCase = UpdateTodoUseCase(repository),
            deleteTodoUseCase = DeleteTodoUseCase(repository),
            toggleTodoDoneUseCase = ToggleTodoDoneUseCase(repository),
            updateSelectedTodoFilterUseCase = UpdateSelectedTodoFilterUseCase(repository),
            updateSelectedTodoPriorityFilterUseCase = UpdateSelectedTodoPriorityFilterUseCase(repository),
            getTodoUseCase = GetTodoUseCase(repository),
            todoReminderScheduler = NoopReminderScheduler
        )
    }

    @Test
    fun addClickOpensEditSheet() = runTest {
        viewModel.onAction(TodoListAction.OnAddClick)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.isEditDialogVisible).isTrue()
        assertThat(viewModel.uiState.value.draftPriority).isEqualTo(TodoPriority.MEDIUM)
    }

    @Test
    fun saveCreatesTodoWithSelectedPriority() = runTest {
        viewModel.onAction(TodoListAction.OnAddClick)
        viewModel.onAction(TodoListAction.OnTitleChange("Pay electricity"))
        viewModel.onAction(TodoListAction.OnPrioritySelectedInEditor(TodoPriority.HIGH))

        viewModel.onAction(TodoListAction.OnSaveClick)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isEditDialogVisible).isFalse()
        assertThat(state.items).hasSize(1)
        assertThat(state.items.first().priority).isEqualTo(TodoPriority.HIGH)
    }

    @Test
    fun priorityFilterShowsOnlyMatchingItems() = runTest {
        repository.addTodo(title = "Low", dueDate = null, categoryId = null, priority = TodoPriority.LOW)
        repository.addTodo(title = "Medium", dueDate = null, categoryId = null, priority = TodoPriority.MEDIUM)
        repository.addTodo(title = "High", dueDate = null, categoryId = null, priority = TodoPriority.HIGH)

        viewModel.onAction(TodoListAction.OnPriorityFilterChange(TodoPriorityFilter.HIGH))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.selectedPriorityFilter).isEqualTo(TodoPriorityFilter.HIGH)
        assertThat(state.items.map { it.title }).containsExactly("High")
    }

    @Test
    fun editFlowUpdatesPriority() = runTest {
        val id = repository.addTodo(
            title = "Design review",
            dueDate = null,
            categoryId = null,
            priority = TodoPriority.LOW
        ).getOrThrow()
        advanceUntilIdle()

        viewModel.onAction(TodoListAction.OnEditClick(id))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isEditDialogVisible).isTrue()
        assertThat(viewModel.uiState.value.draftPriority).isEqualTo(TodoPriority.LOW)

        viewModel.onAction(TodoListAction.OnPrioritySelectedInEditor(TodoPriority.HIGH))
        viewModel.onAction(TodoListAction.OnSaveClick)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.items.first().priority).isEqualTo(TodoPriority.HIGH)
    }

    @Test
    fun dismissClearsDraftState() = runTest {
        viewModel.onAction(TodoListAction.OnAddClick)
        viewModel.onAction(TodoListAction.OnTitleChange("Temp"))
        viewModel.onAction(TodoListAction.OnPrioritySelectedInEditor(TodoPriority.HIGH))

        viewModel.onAction(TodoListAction.OnDismissDialog)

        val state = viewModel.uiState.value
        assertThat(state.isEditDialogVisible).isFalse()
        assertThat(state.draftTitle).isEmpty()
        assertThat(state.draftPriority).isEqualTo(TodoPriority.MEDIUM)
    }

    private data object NoopReminderScheduler : TodoReminderScheduler {
        override suspend fun schedule(todo: com.example.myfirstapp.core.model.TodoItem) = Unit
        override suspend fun cancel(todoId: Long) = Unit
        override suspend fun rescheduleAll() = Unit
    }
}

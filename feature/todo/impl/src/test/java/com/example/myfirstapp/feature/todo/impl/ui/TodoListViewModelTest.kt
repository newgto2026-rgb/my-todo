package com.example.myfirstapp.feature.todo.impl.ui

import app.cash.turbine.test
import com.example.myfirstapp.core.domain.repository.TodoCategoryRepository
import com.example.myfirstapp.core.domain.repository.TodoFilterRepository
import com.example.myfirstapp.core.domain.repository.TodoItemRepository
import com.example.myfirstapp.core.domain.repository.TodoReminderRepository
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
import com.example.myfirstapp.core.model.Category
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoCategoryFilter
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.model.TodoItem
import com.example.myfirstapp.core.testing.rule.MainDispatcherRule
import com.example.myfirstapp.feature.todo.impl.R
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun addClickOpensDialog() = runTest {
        val viewModel = createViewModel(ConfigurableTodoRepository())

        viewModel.onAction(TodoListAction.OnAddClick)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isEditDialogVisible).isTrue()
        assertThat(state.editingItem).isNull()
        assertThat(state.draftTitle).isEmpty()
    }

    @Test
    fun saveActionAddsTodoAndClosesDialog() = runTest {
        val repository = ConfigurableTodoRepository().apply {
            val categoryId = addCategory("Work", null, null).getOrNull()!!
            selectedCategoryIdForTest = categoryId
        }
        val viewModel = createViewModel(repository)

        viewModel.onAction(TodoListAction.OnAddClick)
        viewModel.onAction(TodoListAction.OnTitleChange("테스트"))
        viewModel.onAction(TodoListAction.OnDueDateInputChange("2026-04-10"))
        viewModel.onAction(TodoListAction.OnCategorySelectedInEditor(repository.selectedCategoryIdForTest))
        viewModel.onAction(TodoListAction.OnSaveClick)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.items).hasSize(1)
        assertThat(state.items.first().title).isEqualTo("테스트")
        assertThat(state.items.first().dueDateText).isEqualTo("2026-04-10")
        assertThat(state.items.first().categoryName).isEqualTo("Work")
        assertThat(state.isEditDialogVisible).isFalse()
    }

    @Test
    fun categoryFilterShowsOnlySelectedCategoryItems() = runTest {
        val repository = ConfigurableTodoRepository()
        val workId = repository.addCategory("Work", null, null).getOrNull()!!
        val personalId = repository.addCategory("Personal", null, null).getOrNull()!!
        repository.seed(
            TodoItem(1L, "A", false, null, 1L, 1L, workId),
            TodoItem(2L, "B", false, null, 1L, 1L, personalId)
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(TodoListAction.OnCategoryFilterChange(workId))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.items.map { it.title }).containsExactly("A")
    }

    @Test
    fun deletingSelectedCategoryFallsBackToAllFilter() = runTest {
        val repository = ConfigurableTodoRepository()
        val workId = repository.addCategory("Work", null, null).getOrNull()!!
        val viewModel = createViewModel(repository)

        viewModel.onAction(TodoListAction.OnCategoryFilterChange(workId))
        advanceUntilIdle()
        viewModel.onAction(TodoListAction.OnCategoryDeleteClick(workId))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.selectedCategoryId).isNull()
    }

    @Test
    fun uncategorizedFilterShowsOnlyUncategorizedItems() = runTest {
        val repository = ConfigurableTodoRepository()
        val workId = repository.addCategory("Work", null, null).getOrNull()!!
        repository.seed(
            TodoItem(1L, "No Category", false, null, 1L, 1L, null),
            TodoItem(2L, "Work Task", false, null, 1L, 1L, workId)
        )
        val viewModel = createViewModel(repository)

        viewModel.onAction(
            TodoListAction.OnCategoryFilterChange(TodoCategoryFilter.UNCATEGORIZED_FILTER_ID)
        )
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.items.map { it.title }).containsExactly("No Category")
    }

    @Test
    fun categorySaveFailureEmitsSnackbar() = runTest {
        val repository = ConfigurableTodoRepository().apply { failAddCategory = true }
        val viewModel = createViewModel(repository)

        viewModel.sideEffect.test {
            viewModel.onAction(TodoListAction.OnManageCategoriesClick)
            viewModel.onAction(TodoListAction.OnCategoryNameInputChange("Work"))
            viewModel.onAction(TodoListAction.OnCategorySaveClick)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                TodoListSideEffect.ShowSnackbar(R.string.todo_error_category_save_failed)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveActionWithBlankTitleSetsValidationError() = runTest {
        val viewModel = createViewModel(ConfigurableTodoRepository())

        viewModel.onAction(TodoListAction.OnAddClick)
        viewModel.onAction(TodoListAction.OnTitleChange("   "))
        viewModel.onAction(TodoListAction.OnSaveClick)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.todo_error_title_required)
    }

    @Test
    fun toggleDoneFailureEmitsSnackbar() = runTest {
        val repository = ConfigurableTodoRepository().apply {
            failToggleDone = true
            seed(TodoItem(1L, "A", false, null, 1L, 1L, null))
        }
        val viewModel = createViewModel(repository)

        viewModel.sideEffect.test {
            viewModel.onAction(TodoListAction.OnToggleDone(1L))
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                TodoListSideEffect.ShowSnackbar(R.string.todo_error_toggle_done_failed)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteFailureEmitsSnackbar() = runTest {
        val repository = ConfigurableTodoRepository().apply {
            failDeleteTodo = true
            seed(TodoItem(1L, "A", false, null, 1L, 1L, null))
        }
        val viewModel = createViewModel(repository)

        viewModel.sideEffect.test {
            viewModel.onAction(TodoListAction.OnDeleteClick(1L))
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(
                TodoListSideEffect.ShowSnackbar(R.string.todo_error_delete_failed)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun filterUpdateFailuresEmitSnackbar() = runTest {
        val repository = ConfigurableTodoRepository().apply {
            failSetSelectedFilter = true
            failSetSelectedCategoryFilter = true
        }
        val viewModel = createViewModel(repository)

        viewModel.sideEffect.test {
            viewModel.onAction(TodoListAction.OnFilterChange(TodoFilter.TODAY))
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(
                TodoListSideEffect.ShowSnackbar(R.string.todo_error_filter_change_failed)
            )

            viewModel.onAction(TodoListAction.OnCategoryFilterChange(99L))
            advanceUntilIdle()
            assertThat(awaitItem()).isEqualTo(
                TodoListSideEffect.ShowSnackbar(R.string.todo_error_category_filter_change_failed)
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun editAndDismissDialog_updatesEditorState() = runTest {
        val repository = ConfigurableTodoRepository().apply {
            seed(TodoItem(5L, "Original", false, LocalDate.of(2026, 4, 12), 1L, 1L, null))
        }
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onAction(TodoListAction.OnEditClick(5L))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isEditDialogVisible).isTrue()
        assertThat(viewModel.uiState.value.editingItem?.id).isEqualTo(5L)
        assertThat(viewModel.uiState.value.draftTitle).isEqualTo("Original")

        viewModel.onAction(TodoListAction.OnDismissDialog)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isEditDialogVisible).isFalse()
        assertThat(viewModel.uiState.value.editingItem).isNull()
        assertThat(viewModel.uiState.value.draftTitle).isEmpty()
    }

    private fun createViewModel(repository: ConfigurableTodoRepository): TodoListViewModel =
        TodoListViewModel(
            observeTodosUseCase = ObserveTodosUseCase(repository),
            observeSelectedTodoFilterUseCase = ObserveSelectedTodoFilterUseCase(repository),
            observeCategoriesUseCase = ObserveCategoriesUseCase(repository),
            observeSelectedCategoryFilterUseCase = ObserveSelectedCategoryFilterUseCase(repository),
            addTodoUseCase = AddTodoUseCase(repository),
            updateTodoUseCase = UpdateTodoUseCase(repository),
            deleteTodoUseCase = DeleteTodoUseCase(repository),
            toggleTodoDoneUseCase = ToggleTodoDoneUseCase(repository),
            updateSelectedTodoFilterUseCase = UpdateSelectedTodoFilterUseCase(repository),
            updateSelectedCategoryFilterUseCase = UpdateSelectedCategoryFilterUseCase(repository),
            addCategoryUseCase = AddCategoryUseCase(repository),
            updateCategoryUseCase = UpdateCategoryUseCase(repository),
            deleteCategoryUseCase = DeleteCategoryUseCase(repository),
            getTodoUseCase = GetTodoUseCase(repository),
            todoReminderScheduler = NoOpTodoReminderScheduler
        )

    private class ConfigurableTodoRepository :
        TodoItemRepository,
        TodoCategoryRepository,
        TodoFilterRepository,
        TodoReminderRepository {
        private val todos = MutableStateFlow<List<TodoItem>>(emptyList())
        private val selectedFilter = MutableStateFlow(TodoFilter.ALL)
        private val categories = MutableStateFlow<List<Category>>(emptyList())
        private val selectedCategoryFilter = MutableStateFlow<Long?>(null)
        private var idSeed = 1L
        private var categoryIdSeed = 1L

        var selectedCategoryIdForTest: Long? = null
        var failAddCategory = false
        var failToggleDone = false
        var failDeleteTodo = false
        var failSetSelectedFilter = false
        var failSetSelectedCategoryFilter = false

        fun seed(vararg items: TodoItem) {
            todos.value = items.toList()
            idSeed = (items.maxOfOrNull { it.id } ?: 0L) + 1L
        }

        override fun observeTodos(): Flow<List<TodoItem>> = todos.asStateFlow()

        override suspend fun getTodo(id: Long): TodoItem? = todos.value.firstOrNull { it.id == id }

        override suspend fun addTodo(
            title: String,
            dueDate: LocalDate?,
            categoryId: Long?,
            reminderAtEpochMillis: Long?,
            isReminderEnabled: Boolean,
            reminderRepeatType: ReminderRepeatType,
            reminderRepeatDaysMask: Int
        ): Result<Long> {
            val now = System.currentTimeMillis()
            val id = idSeed++
            val item = TodoItem(
                id = id,
                title = title,
                isDone = false,
                dueDate = dueDate,
                createdAt = now,
                updatedAt = now,
                categoryId = categoryId,
                reminderAtEpochMillis = reminderAtEpochMillis,
                isReminderEnabled = isReminderEnabled,
                reminderRepeatType = reminderRepeatType,
                reminderRepeatDaysMask = reminderRepeatDaysMask
            )
            todos.value = listOf(item) + todos.value
            return Result.success(id)
        }

        override suspend fun updateTodo(
            id: Long,
            title: String,
            dueDate: LocalDate?,
            categoryId: Long?,
            reminderAtEpochMillis: Long?,
            isReminderEnabled: Boolean,
            reminderRepeatType: ReminderRepeatType,
            reminderRepeatDaysMask: Int
        ): Result<Unit> {
            val existing = getTodo(id) ?: return Result.failure(IllegalStateException("not found"))
            todos.value = todos.value.map {
                if (it.id == id) {
                    existing.copy(
                        title = title,
                        dueDate = dueDate,
                        updatedAt = System.currentTimeMillis(),
                        categoryId = categoryId,
                        reminderAtEpochMillis = reminderAtEpochMillis,
                        isReminderEnabled = isReminderEnabled,
                        reminderRepeatType = reminderRepeatType,
                        reminderRepeatDaysMask = reminderRepeatDaysMask
                    )
                } else {
                    it
                }
            }
            return Result.success(Unit)
        }

        override suspend fun deleteTodo(id: Long): Result<Unit> {
            if (failDeleteTodo) return Result.failure(IllegalStateException("delete failed"))
            todos.value = todos.value.filterNot { it.id == id }
            return Result.success(Unit)
        }

        override suspend fun toggleTodoDone(id: Long): Result<Unit> {
            if (failToggleDone) return Result.failure(IllegalStateException("toggle failed"))
            todos.value = todos.value.map {
                if (it.id == id) it.copy(isDone = !it.isDone, updatedAt = System.currentTimeMillis()) else it
            }
            return Result.success(Unit)
        }

        override suspend fun getTodosWithActiveReminder(): List<TodoItem> =
            todos.value
                .asSequence()
                .filter { it.isReminderEnabled && it.reminderAtEpochMillis != null }
                .sortedBy { it.reminderAtEpochMillis }
                .toList()

        override fun observeSelectedFilter(): Flow<TodoFilter> = selectedFilter.asStateFlow()

        override suspend fun setSelectedFilter(filter: TodoFilter): Result<Unit> {
            if (failSetSelectedFilter) return Result.failure(IllegalStateException("filter failed"))
            selectedFilter.value = filter
            return Result.success(Unit)
        }

        override fun observeCategories(): Flow<List<Category>> = categories.asStateFlow()

        override suspend fun addCategory(name: String, colorHex: String?, icon: String?): Result<Long> {
            if (failAddCategory) return Result.failure(IllegalStateException("add category failed"))
            val now = System.currentTimeMillis()
            val id = categoryIdSeed++
            categories.value = categories.value + Category(id, name, colorHex, icon, now, now)
            return Result.success(id)
        }

        override suspend fun updateCategory(id: Long, name: String, colorHex: String?, icon: String?): Result<Unit> {
            val existing = categories.value.firstOrNull { it.id == id }
                ?: return Result.failure(IllegalStateException("not found"))
            categories.value = categories.value.map {
                if (it.id == id) existing.copy(name = name, colorHex = colorHex, icon = icon, updatedAt = System.currentTimeMillis()) else it
            }
            return Result.success(Unit)
        }

        override suspend fun deleteCategory(id: Long): Result<Unit> {
            categories.value = categories.value.filterNot { it.id == id }
            todos.value = todos.value.map { item ->
                if (item.categoryId == id) item.copy(categoryId = null, updatedAt = System.currentTimeMillis()) else item
            }
            if (selectedCategoryFilter.value == id) {
                selectedCategoryFilter.value = null
            }
            return Result.success(Unit)
        }

        override fun observeSelectedCategoryFilter(): Flow<Long?> = selectedCategoryFilter.asStateFlow()

        override suspend fun setSelectedCategoryFilter(categoryId: Long?): Result<Unit> {
            if (failSetSelectedCategoryFilter) return Result.failure(IllegalStateException("category filter failed"))
            selectedCategoryFilter.value = categoryId
            return Result.success(Unit)
        }
    }

    private object NoOpTodoReminderScheduler : TodoReminderScheduler {
        override suspend fun schedule(todo: TodoItem) = Unit
        override suspend fun cancel(todoId: Long) = Unit
        override suspend fun rescheduleAll() = Unit
    }
}

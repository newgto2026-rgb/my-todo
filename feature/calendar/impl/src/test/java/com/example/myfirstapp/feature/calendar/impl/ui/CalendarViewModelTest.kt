package com.example.myfirstapp.feature.calendar.impl.ui

import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodoSummariesUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodosUseCase
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.testing.repository.FakeTodoRepository
import com.example.myfirstapp.core.testing.rule.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import kotlin.math.min

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun initialState_hasCurrentMonthAndSelectedDate() = runTest {
        val viewModel = createViewModel(FakeTodoRepository())

        val state = viewModel.uiState.value
        val today = LocalDate.now()

        assertThat(state.currentMonth).isEqualTo(java.time.YearMonth.from(today))
        assertThat(state.selectedDate).isEqualTo(today)
    }

    @Test
    fun nextMonthAction_movesMonthAndKeepsDayInRange() = runTest {
        val viewModel = createViewModel(FakeTodoRepository())
        val before = viewModel.uiState.value

        viewModel.onAction(CalendarAction.OnNextMonthClick)
        advanceUntilIdle()

        val after = viewModel.uiState.value
        val expectedMonth = before.currentMonth.plusMonths(1)
        val expectedDay = min(before.selectedDate.dayOfMonth, expectedMonth.lengthOfMonth())

        assertThat(after.currentMonth).isEqualTo(expectedMonth)
        assertThat(after.selectedDate).isEqualTo(expectedMonth.atDay(expectedDay))
    }

    @Test
    fun dateClickAction_updatesSelectedDate() = runTest {
        val viewModel = createViewModel(FakeTodoRepository())
        val targetDate = viewModel.uiState.value.currentMonth.atDay(15)

        viewModel.onAction(CalendarAction.OnDateClick(targetDate))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.selectedDate).isEqualTo(targetDate)
    }

    @Test
    fun todoClickAction_emitsNavigateSideEffect() = runTest {
        val repository = FakeTodoRepository()
        val viewModel = createViewModel(repository)
        val targetDate = viewModel.uiState.value.currentMonth.atDay(10)

        val todoId = repository.addTodo(
            title = "In month",
            dueDate = targetDate,
            categoryId = null,
            reminderAtEpochMillis = null,
            isReminderEnabled = false,
            reminderRepeatType = ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        ).getOrThrow()

        advanceUntilIdle()
        viewModel.onAction(CalendarAction.OnDateClick(targetDate))
        advanceUntilIdle()

        val emitted = async { viewModel.sideEffect.first() }
        viewModel.onAction(CalendarAction.OnTodoClick(todoId))
        advanceUntilIdle()

        assertThat(emitted.await()).isEqualTo(CalendarSideEffect.NavigateToTodoEdit(todoId))
    }

    @Test
    fun selectedDateTodos_includeOnlySelectedDateTodos() = runTest {
        val repository = FakeTodoRepository()
        val viewModel = createViewModel(repository)
        val currentMonth = viewModel.uiState.value.currentMonth
        val selectedDate = currentMonth.atDay(10)
        val anotherDate = currentMonth.atDay(11)

        repository.addTodo(
            title = "Selected date todo",
            dueDate = selectedDate,
            categoryId = null,
            reminderAtEpochMillis = null,
            isReminderEnabled = false,
            reminderRepeatType = ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        )
        repository.addTodo(
            title = "Another date todo",
            dueDate = anotherDate,
            categoryId = null,
            reminderAtEpochMillis = null,
            isReminderEnabled = false,
            reminderRepeatType = ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        )
        advanceUntilIdle()

        viewModel.onAction(CalendarAction.OnDateClick(selectedDate))
        advanceUntilIdle()

        val todos = viewModel.uiState.value.selectedDateTodos
        assertThat(todos).hasSize(1)
        assertThat(todos.first().title).isEqualTo("Selected date todo")
    }

    @Test
    fun summaries_includeOnlyCurrentMonthTodos() = runTest {
        val repository = FakeTodoRepository()
        val viewModel = createViewModel(repository)
        val currentMonth = viewModel.uiState.value.currentMonth
        val inMonthDate = currentMonth.atDay(10)
        val outOfMonthDate = currentMonth.plusMonths(1).atDay(10)

        repository.addTodo(
            title = "In month",
            dueDate = inMonthDate,
            categoryId = null,
            reminderAtEpochMillis = null,
            isReminderEnabled = false,
            reminderRepeatType = ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        )
        repository.addTodo(
            title = "Out of month",
            dueDate = outOfMonthDate,
            categoryId = null,
            reminderAtEpochMillis = null,
            isReminderEnabled = false,
            reminderRepeatType = ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.summariesByDate.keys).contains(inMonthDate)
        assertThat(state.summariesByDate.keys).doesNotContain(outOfMonthDate)
        assertThat(state.summariesByDate[inMonthDate]?.indicatorCount).isEqualTo(1)
    }

    @Test
    fun selectedDateTodos_mapReminderFields() = runTest {
        val repository = FakeTodoRepository()
        val viewModel = createViewModel(repository)
        val selectedDate = viewModel.uiState.value.currentMonth.atDay(8)

        repository.addTodo(
            title = "Reminder mapped",
            dueDate = selectedDate,
            categoryId = null,
            dueTimeMinutes = 9 * 60 + 30,
            reminderAtEpochMillis = 0L,
            isReminderEnabled = true,
            reminderRepeatType = ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0,
            reminderLeadMinutes = 10
        )
        advanceUntilIdle()

        viewModel.onAction(CalendarAction.OnDateClick(selectedDate))
        advanceUntilIdle()

        val todo = viewModel.uiState.value.selectedDateTodos.single()
        assertThat(todo.isReminderEnabled).isTrue()
        assertThat(todo.reminderLeadMinutes).isEqualTo(10)
        assertThat(todo.dueTimeLabel).isNotNull()
    }

    private fun createViewModel(repository: FakeTodoRepository): CalendarViewModel =
        CalendarViewModel(
            observeMonthlyTodoSummariesUseCase = ObserveMonthlyTodoSummariesUseCase(
                observeMonthlyTodosUseCase = ObserveMonthlyTodosUseCase(repository)
            ),
            observeMonthlyTodosUseCase = ObserveMonthlyTodosUseCase(repository)
        )
}

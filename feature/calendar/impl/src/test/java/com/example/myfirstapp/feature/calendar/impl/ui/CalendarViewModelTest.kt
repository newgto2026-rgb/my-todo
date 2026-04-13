package com.example.myfirstapp.feature.calendar.impl.ui

import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodoSummariesUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodosUseCase
import com.example.myfirstapp.core.testing.repository.FakeTodoRepository
import com.example.myfirstapp.core.testing.rule.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            reminderRepeatType = com.example.myfirstapp.core.model.ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        )
        repository.addTodo(
            title = "Out of month",
            dueDate = outOfMonthDate,
            categoryId = null,
            reminderAtEpochMillis = null,
            isReminderEnabled = false,
            reminderRepeatType = com.example.myfirstapp.core.model.ReminderRepeatType.NONE,
            reminderRepeatDaysMask = 0
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.summariesByDate.keys).contains(inMonthDate)
        assertThat(state.summariesByDate.keys).doesNotContain(outOfMonthDate)
        assertThat(state.summariesByDate[inMonthDate]?.indicatorCount).isEqualTo(1)
    }

    private fun createViewModel(repository: FakeTodoRepository): CalendarViewModel =
        CalendarViewModel(
            observeMonthlyTodoSummariesUseCase = ObserveMonthlyTodoSummariesUseCase(
                observeMonthlyTodosUseCase = ObserveMonthlyTodosUseCase(repository)
            )
        )
}

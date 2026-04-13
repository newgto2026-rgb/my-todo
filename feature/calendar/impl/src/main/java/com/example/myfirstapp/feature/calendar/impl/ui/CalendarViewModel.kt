package com.example.myfirstapp.feature.calendar.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodoSummariesUseCase
import com.example.myfirstapp.core.model.DateTodoSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.min

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel @Inject constructor(
    observeMonthlyTodoSummariesUseCase: ObserveMonthlyTodoSummariesUseCase
) : ViewModel() {

    private val monthState = MutableStateFlow(YearMonth.now())
    private val selectedDateState = MutableStateFlow(LocalDate.now())

    private val summariesByDate = monthState
        .flatMapLatest { yearMonth -> observeMonthlyTodoSummariesUseCase(yearMonth) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    val uiState: StateFlow<CalendarUiState> = combine(
        monthState,
        selectedDateState,
        summariesByDate
    ) { currentMonth, selectedDate, summaries ->
        val adjustedSelectedDate = selectedDate.normalizeToMonth(currentMonth)
        if (adjustedSelectedDate != selectedDateState.value) {
            selectedDateState.value = adjustedSelectedDate
        }
        CalendarUiState(
            currentMonth = currentMonth,
            selectedDate = adjustedSelectedDate,
            days = buildMonthCells(
                yearMonth = currentMonth,
                selectedDate = adjustedSelectedDate,
                today = LocalDate.now(),
                summariesByDate = summaries
            ),
            summariesByDate = summaries
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CalendarUiState(
            currentMonth = monthState.value,
            selectedDate = selectedDateState.value.normalizeToMonth(monthState.value),
            days = emptyList(),
            summariesByDate = emptyMap()
        )
    )

    fun onAction(action: CalendarAction) {
        when (action) {
            CalendarAction.OnNextMonthClick -> moveMonthBy(1)
            CalendarAction.OnPreviousMonthClick -> moveMonthBy(-1)
            is CalendarAction.OnDateClick -> {
                selectedDateState.value = action.date
            }
        }
    }

    private fun moveMonthBy(offsetMonths: Long) {
        val newMonth = monthState.value.plusMonths(offsetMonths)
        monthState.value = newMonth
        selectedDateState.value = selectedDateState.value.normalizeToMonth(newMonth)
    }
}

private fun buildMonthCells(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    summariesByDate: Map<LocalDate, DateTodoSummary>
): List<CalendarDayUiModel> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val firstDate = yearMonth.atDay(1)
    val leadingBlanks = firstDate.dayOfWeek.distanceFrom(firstDayOfWeek)
    val daysInMonth = yearMonth.lengthOfMonth()
    val totalCells = ((leadingBlanks + daysInMonth + 6) / 7) * 7

    return List(totalCells) { index ->
        val dayOfMonth = index - leadingBlanks + 1
        if (dayOfMonth in 1..daysInMonth) {
            val date = yearMonth.atDay(dayOfMonth)
            val summary = summariesByDate[date]
            CalendarDayUiModel(
                date = date,
                isCurrentMonth = true,
                isToday = date == today,
                isSelected = date == selectedDate,
                indicatorCount = summary?.indicatorCount ?: 0,
                overflowCount = summary?.overflowCount ?: 0
            )
        } else {
            CalendarDayUiModel(
                date = null,
                isCurrentMonth = false,
                isToday = false,
                isSelected = false,
                indicatorCount = 0,
                overflowCount = 0
            )
        }
    }
}

private fun LocalDate.normalizeToMonth(targetMonth: YearMonth): LocalDate {
    val normalizedDay = min(dayOfMonth, targetMonth.lengthOfMonth())
    return targetMonth.atDay(normalizedDay)
}

private fun DayOfWeek.distanceFrom(other: DayOfWeek): Int =
    (value - other.value + 7) % 7

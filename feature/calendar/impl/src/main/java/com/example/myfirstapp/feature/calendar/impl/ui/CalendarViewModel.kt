package com.example.myfirstapp.feature.calendar.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodoSummariesUseCase
import com.example.myfirstapp.core.domain.usecase.ObserveMonthlyTodosUseCase
import com.example.myfirstapp.core.model.DateTodoSummary
import com.example.myfirstapp.core.model.TodoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.min

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel @Inject constructor(
    observeMonthlyTodoSummariesUseCase: ObserveMonthlyTodoSummariesUseCase,
    observeMonthlyTodosUseCase: ObserveMonthlyTodosUseCase
) : ViewModel() {

    private val monthState = MutableStateFlow(YearMonth.now())
    private val selectedDateState = MutableStateFlow(LocalDate.now())
    private val isDayTodoSheetVisibleState = MutableStateFlow(false)
    private val sideEffectMutable = MutableSharedFlow<CalendarSideEffect>()

    val sideEffect = sideEffectMutable.asSharedFlow()

    private val monthlyTodos = monthState
        .flatMapLatest { yearMonth -> observeMonthlyTodosUseCase(yearMonth) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val summariesByDate = monthState
        .flatMapLatest { yearMonth -> observeMonthlyTodoSummariesUseCase(yearMonth) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    private val selectedDateTodos = combine(
        selectedDateState,
        monthlyTodos
    ) { selectedDate, todos ->
        todos
            .asSequence()
            .filter { it.dueDate == selectedDate }
            .sortedWith(compareBy<TodoItem> { it.isDone }.thenBy { it.id })
            .map { it.toSelectedTodoUiModel() }
            .toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val uiState: StateFlow<CalendarUiState> = combine(
        monthState,
        selectedDateState,
        summariesByDate,
        selectedDateTodos,
        isDayTodoSheetVisibleState
    ) { currentMonth, selectedDate, summaries, dateTodos, isSheetVisible ->
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
            summariesByDate = summaries,
            selectedDateTodos = dateTodos,
            isDayTodoSheetVisible = isSheetVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CalendarUiState(
            currentMonth = monthState.value,
            selectedDate = selectedDateState.value.normalizeToMonth(monthState.value),
            days = emptyList(),
            summariesByDate = emptyMap(),
            selectedDateTodos = emptyList(),
            isDayTodoSheetVisible = false
        )
    )

    fun onAction(action: CalendarAction) {
        when (action) {
            CalendarAction.OnNextMonthClick -> moveMonthBy(1)
            CalendarAction.OnPreviousMonthClick -> moveMonthBy(-1)
            is CalendarAction.OnDateClick -> {
                selectedDateState.value = action.date
                isDayTodoSheetVisibleState.value = true
            }

            CalendarAction.OnBottomSheetDismiss -> {
                isDayTodoSheetVisibleState.value = false
            }

            is CalendarAction.OnTodoClick -> {
                viewModelScope.launch {
                    sideEffectMutable.emit(CalendarSideEffect.NavigateToTodoEdit(action.todoId))
                }
                isDayTodoSheetVisibleState.value = false
            }
        }
    }

    private fun moveMonthBy(offsetMonths: Long) {
        val newMonth = monthState.value.plusMonths(offsetMonths)
        monthState.value = newMonth
        selectedDateState.value = selectedDateState.value.normalizeToMonth(newMonth)
    }
}

internal fun buildMonthCells(
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

internal fun LocalDate.normalizeToMonth(targetMonth: YearMonth): LocalDate {
    val normalizedDay = min(dayOfMonth, targetMonth.lengthOfMonth())
    return targetMonth.atDay(normalizedDay)
}

internal fun DayOfWeek.distanceFrom(other: DayOfWeek): Int =
    (value - other.value + 7) % 7

internal fun TodoItem.toSelectedTodoUiModel(): CalendarSelectedTodoUiModel =
    CalendarSelectedTodoUiModel(
        id = id,
        title = title,
        isDone = isDone,
        isReminderEnabled = isReminderEnabled,
        dueTimeLabel = dueTimeMinutes?.let(::formatLocalTimeFromMinutes)
            ?: reminderAtEpochMillis?.let(::formatLocalTimeFromEpochMillis),
        reminderLeadMinutes = reminderLeadMinutes
    )

private fun formatLocalTimeFromMinutes(minutes: Int): String {
    val normalized = ((minutes % (24 * 60)) + (24 * 60)) % (24 * 60)
    return java.time.LocalTime.of(normalized / 60, normalized % 60)
        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
}

private fun formatLocalTimeFromEpochMillis(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
        .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

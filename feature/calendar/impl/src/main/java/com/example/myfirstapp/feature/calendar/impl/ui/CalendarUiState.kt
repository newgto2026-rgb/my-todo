package com.example.myfirstapp.feature.calendar.impl.ui

import androidx.compose.runtime.Immutable
import com.example.myfirstapp.core.model.DateTodoSummary
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class CalendarUiState(
    val currentMonth: YearMonth,
    val selectedDate: LocalDate,
    val days: List<CalendarDayUiModel>,
    val summariesByDate: Map<LocalDate, DateTodoSummary>,
    val selectedDateTodos: List<CalendarSelectedTodoUiModel>,
    val isDayTodoSheetVisible: Boolean
)

@Immutable
data class CalendarDayUiModel(
    val date: LocalDate?,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val indicatorCount: Int,
    val overflowCount: Int
)

@Immutable
data class CalendarSelectedTodoUiModel(
    val id: Long,
    val title: String,
    val isDone: Boolean,
    val isReminderEnabled: Boolean,
    val dueTimeLabel: String?,
    val reminderLeadMinutes: Int?
)

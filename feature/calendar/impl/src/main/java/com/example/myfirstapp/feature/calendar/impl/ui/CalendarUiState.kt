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
    val summariesByDate: Map<LocalDate, DateTodoSummary>
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

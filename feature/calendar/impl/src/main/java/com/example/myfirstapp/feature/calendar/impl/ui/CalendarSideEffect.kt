package com.example.myfirstapp.feature.calendar.impl.ui

sealed interface CalendarSideEffect {
    data class NavigateToTodoEdit(val todoId: Long) : CalendarSideEffect
}

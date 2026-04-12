package com.example.myfirstapp.feature.todo.impl.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal data class TodoDraftValidationResult(
    val normalizedTitle: String? = null,
    val parsedDueDate: LocalDate? = null,
    val reminderAtEpochMillis: Long? = null,
    val errorMessage: String? = null
)

internal fun validateTodoDraft(state: TodoListUiState): TodoDraftValidationResult {
    val normalizedTitle = state.draftTitle.trim()
    if (normalizedTitle.isBlank()) {
        return TodoDraftValidationResult(errorMessage = "제목을 입력해주세요.")
    }

    val parsedDueDate = parseIsoDateInput(state.draftDueDateInput)
    if (state.draftDueDateInput.isNotBlank() && parsedDueDate == null) {
        return TodoDraftValidationResult(errorMessage = "날짜 형식은 yyyy-MM-dd 입니다.")
    }

    val reminderAtEpochMillis = reminderDateTimeToEpochMillis(state.draftReminderDateTimeInput)
    if (state.draftReminderEnabled && reminderAtEpochMillis == null) {
        return TodoDraftValidationResult(errorMessage = "리마인더 형식은 yyyy-MM-dd HH:mm 입니다.")
    }

    return TodoDraftValidationResult(
        normalizedTitle = normalizedTitle,
        parsedDueDate = parsedDueDate,
        reminderAtEpochMillis = reminderAtEpochMillis
    )
}

internal fun parseIsoDateInput(value: String): LocalDate? {
    if (value.isBlank()) return null
    return runCatching { LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull()
}

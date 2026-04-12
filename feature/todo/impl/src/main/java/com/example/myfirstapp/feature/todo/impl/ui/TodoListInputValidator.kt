package com.example.myfirstapp.feature.todo.impl.ui

import androidx.annotation.StringRes
import com.example.myfirstapp.feature.todo.impl.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal data class TodoDraftValidationResult(
    val normalizedTitle: String? = null,
    val parsedDueDate: LocalDate? = null,
    val reminderAtEpochMillis: Long? = null,
    @StringRes val errorMessageRes: Int? = null
)

internal fun validateTodoDraft(state: TodoListUiState): TodoDraftValidationResult {
    val normalizedTitle = state.draftTitle.trim()
    if (normalizedTitle.isBlank()) {
        return TodoDraftValidationResult(errorMessageRes = R.string.todo_error_title_required)
    }

    val parsedDueDate = parseIsoDateInput(state.draftDueDateInput)
    if (state.draftDueDateInput.isNotBlank() && parsedDueDate == null) {
        return TodoDraftValidationResult(errorMessageRes = R.string.todo_error_due_date_format)
    }

    val reminderAtEpochMillis = reminderDateTimeToEpochMillis(state.draftReminderDateTimeInput)
    if (state.draftReminderEnabled && reminderAtEpochMillis == null) {
        return TodoDraftValidationResult(errorMessageRes = R.string.todo_error_reminder_format)
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

package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoRepository
import com.example.myfirstapp.core.model.ReminderRepeatType
import java.time.LocalDate
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        dueDate: LocalDate?,
        categoryId: Long?,
        reminderAtEpochMillis: Long? = null,
        isReminderEnabled: Boolean = false,
        reminderRepeatType: ReminderRepeatType = ReminderRepeatType.NONE,
        reminderRepeatDaysMask: Int = 0
    ): Result<Unit> {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Title must not be blank"))
        }
        return repository.updateTodo(
            id = id,
            title = normalizedTitle,
            dueDate = dueDate,
            categoryId = categoryId,
            reminderAtEpochMillis = reminderAtEpochMillis,
            isReminderEnabled = isReminderEnabled,
            reminderRepeatType = reminderRepeatType,
            reminderRepeatDaysMask = reminderRepeatDaysMask
        )
    }
}

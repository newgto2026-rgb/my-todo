package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoItemRepository
import com.example.myfirstapp.core.model.ReminderRepeatType
import java.time.LocalDate
import javax.inject.Inject

class AddTodoUseCase @Inject constructor(
    private val repository: TodoItemRepository
) {
    suspend operator fun invoke(
        title: String,
        dueDate: LocalDate?,
        categoryId: Long?,
        reminderAtEpochMillis: Long? = null,
        isReminderEnabled: Boolean = false,
        reminderRepeatType: ReminderRepeatType = ReminderRepeatType.NONE,
        reminderRepeatDaysMask: Int = 0
    ): Result<Long> {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Title must not be blank"))
        }
        return repository.addTodo(
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

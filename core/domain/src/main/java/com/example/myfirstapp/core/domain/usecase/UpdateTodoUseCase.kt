package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoItemRepository
import com.example.myfirstapp.core.model.ReminderRepeatType
import java.time.LocalDate
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(
    private val repository: TodoItemRepository
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        dueDate: LocalDate?,
        categoryId: Long?,
        dueTimeMinutes: Int? = null,
        reminderAtEpochMillis: Long? = null,
        isReminderEnabled: Boolean = false,
        reminderRepeatType: ReminderRepeatType = ReminderRepeatType.NONE,
        reminderRepeatDaysMask: Int = 0,
        reminderLeadMinutes: Int? = null
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
            dueTimeMinutes = dueTimeMinutes,
            reminderAtEpochMillis = reminderAtEpochMillis,
            isReminderEnabled = isReminderEnabled,
            reminderRepeatType = reminderRepeatType,
            reminderRepeatDaysMask = reminderRepeatDaysMask,
            reminderLeadMinutes = reminderLeadMinutes
        )
    }
}

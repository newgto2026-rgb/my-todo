package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.ReminderRepository
import com.example.myfirstapp.core.model.ReminderRepeatType
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(
        id: Long,
        title: String,
        note: String?,
        triggerAtEpochMillis: Long,
        repeatType: ReminderRepeatType,
        repeatDaysMask: Int,
        isEnabled: Boolean
    ): Result<Unit> {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Title must not be blank"))
        }

        return repository.updateReminder(
            id = id,
            title = normalizedTitle,
            note = note?.trim()?.ifBlank { null },
            triggerAtEpochMillis = triggerAtEpochMillis,
            repeatType = repeatType,
            repeatDaysMask = repeatDaysMask,
            isEnabled = isEnabled
        )
    }
}

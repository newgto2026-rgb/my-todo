package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.ReminderRepository
import javax.inject.Inject

class SnoozeReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: Long, minutes: Int): Result<Unit> {
        if (minutes <= 0) {
            return Result.failure(IllegalArgumentException("Minutes must be positive"))
        }
        return repository.snoozeReminder(id, minutes)
    }
}

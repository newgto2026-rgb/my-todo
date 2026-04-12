package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.ReminderRepository
import javax.inject.Inject

class DeleteReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.deleteReminder(id)
}

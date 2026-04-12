package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.ReminderRepository
import com.example.myfirstapp.core.model.Reminder
import javax.inject.Inject

class GetActiveRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(): List<Reminder> = repository.getActiveReminders()
}

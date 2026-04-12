package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.ReminderRepository
import com.example.myfirstapp.core.model.Reminder
import javax.inject.Inject

class GetReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(id: Long): Reminder? = repository.getReminder(id)
}

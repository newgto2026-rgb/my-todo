package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.ReminderRepository
import com.example.myfirstapp.core.model.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(): Flow<List<Reminder>> = repository.observeReminders()
}

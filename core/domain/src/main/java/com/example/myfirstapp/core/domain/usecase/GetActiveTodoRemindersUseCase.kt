package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoReminderRepository
import com.example.myfirstapp.core.model.TodoItem
import javax.inject.Inject

class GetActiveTodoRemindersUseCase @Inject constructor(
    private val repository: TodoReminderRepository
) {
    suspend operator fun invoke(): List<TodoItem> = repository.getTodosWithActiveReminder()
}

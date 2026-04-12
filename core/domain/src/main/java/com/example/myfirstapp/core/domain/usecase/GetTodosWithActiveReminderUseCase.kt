package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoRepository
import com.example.myfirstapp.core.model.TodoItem
import javax.inject.Inject

class GetTodosWithActiveReminderUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(): List<TodoItem> = repository.getTodosWithActiveReminder()
}

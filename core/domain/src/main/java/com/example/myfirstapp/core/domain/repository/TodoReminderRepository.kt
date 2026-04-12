package com.example.myfirstapp.core.domain.repository

import com.example.myfirstapp.core.model.TodoItem

interface TodoReminderRepository {
    suspend fun getTodosWithActiveReminder(): List<TodoItem>
}

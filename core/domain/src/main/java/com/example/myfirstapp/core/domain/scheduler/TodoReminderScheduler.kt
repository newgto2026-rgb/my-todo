package com.example.myfirstapp.core.domain.scheduler

import com.example.myfirstapp.core.model.TodoItem

interface TodoReminderScheduler {
    suspend fun schedule(todo: TodoItem)
    suspend fun cancel(todoId: Long)
    suspend fun rescheduleAll()
}

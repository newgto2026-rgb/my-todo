package com.example.myfirstapp.core.domain.repository

import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TodoItemRepository {
    fun observeTodos(): Flow<List<TodoItem>>
    suspend fun getTodo(id: Long): TodoItem?

    suspend fun addTodo(
        title: String,
        dueDate: LocalDate?,
        categoryId: Long?,
        reminderAtEpochMillis: Long? = null,
        isReminderEnabled: Boolean = false,
        reminderRepeatType: ReminderRepeatType = ReminderRepeatType.NONE,
        reminderRepeatDaysMask: Int = 0
    ): Result<Long>

    suspend fun updateTodo(
        id: Long,
        title: String,
        dueDate: LocalDate?,
        categoryId: Long?,
        reminderAtEpochMillis: Long? = null,
        isReminderEnabled: Boolean = false,
        reminderRepeatType: ReminderRepeatType = ReminderRepeatType.NONE,
        reminderRepeatDaysMask: Int = 0
    ): Result<Unit>

    suspend fun deleteTodo(id: Long): Result<Unit>
    suspend fun toggleTodoDone(id: Long): Result<Unit>
}

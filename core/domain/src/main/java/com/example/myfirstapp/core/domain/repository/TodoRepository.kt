package com.example.myfirstapp.core.domain.repository

import com.example.myfirstapp.core.model.Category
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TodoRepository {
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
    suspend fun getTodosWithActiveReminder(): List<TodoItem>

    fun observeSelectedFilter(): Flow<TodoFilter>
    suspend fun setSelectedFilter(filter: TodoFilter): Result<Unit>

    fun observeCategories(): Flow<List<Category>>
    suspend fun addCategory(name: String, colorHex: String?, icon: String?): Result<Long>
    suspend fun updateCategory(id: Long, name: String, colorHex: String?, icon: String?): Result<Unit>
    suspend fun deleteCategory(id: Long): Result<Unit>

    fun observeSelectedCategoryFilter(): Flow<Long?>
    suspend fun setSelectedCategoryFilter(categoryId: Long?): Result<Unit>
}

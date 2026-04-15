package com.example.myfirstapp.core.domain.repository

import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.model.TodoPriorityFilter
import kotlinx.coroutines.flow.Flow

interface TodoFilterRepository {
    fun observeSelectedFilter(): Flow<TodoFilter>
    suspend fun setSelectedFilter(filter: TodoFilter): Result<Unit>

    fun observeSelectedCategoryFilter(): Flow<Long?>
    suspend fun setSelectedCategoryFilter(categoryId: Long?): Result<Unit>

    fun observeSelectedPriorityFilter(): Flow<TodoPriorityFilter>
    suspend fun setSelectedPriorityFilter(filter: TodoPriorityFilter): Result<Unit>
}

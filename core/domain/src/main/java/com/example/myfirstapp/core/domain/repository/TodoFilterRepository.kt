package com.example.myfirstapp.core.domain.repository

import com.example.myfirstapp.core.model.TodoFilter
import kotlinx.coroutines.flow.Flow

interface TodoFilterRepository {
    fun observeSelectedFilter(): Flow<TodoFilter>
    suspend fun setSelectedFilter(filter: TodoFilter): Result<Unit>

    fun observeSelectedCategoryFilter(): Flow<Long?>
    suspend fun setSelectedCategoryFilter(categoryId: Long?): Result<Unit>
}

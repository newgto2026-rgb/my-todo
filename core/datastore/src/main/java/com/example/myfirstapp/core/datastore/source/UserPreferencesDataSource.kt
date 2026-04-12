package com.example.myfirstapp.core.datastore.source

import com.example.myfirstapp.core.model.TodoFilter
import kotlinx.coroutines.flow.Flow

interface UserPreferencesDataSource {
    val selectedTodoFilter: Flow<TodoFilter>
    val selectedTodoCategoryFilter: Flow<Long?>
    suspend fun setSelectedTodoFilter(filter: TodoFilter)
    suspend fun setSelectedTodoCategoryFilter(categoryId: Long?)
}

package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoItemRepository
import com.example.myfirstapp.core.model.TodoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTodosUseCase @Inject constructor(
    private val repository: TodoItemRepository
) {
    operator fun invoke(): Flow<List<TodoItem>> = repository.observeTodos()
}

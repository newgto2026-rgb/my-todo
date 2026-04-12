package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoRepository
import com.example.myfirstapp.core.model.TodoItem
import javax.inject.Inject

class GetTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Long): TodoItem? = repository.getTodo(id)
}

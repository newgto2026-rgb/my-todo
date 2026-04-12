package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoRepository
import com.example.myfirstapp.core.model.TodoFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedTodoFilterUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    operator fun invoke(): Flow<TodoFilter> = repository.observeSelectedFilter()
}

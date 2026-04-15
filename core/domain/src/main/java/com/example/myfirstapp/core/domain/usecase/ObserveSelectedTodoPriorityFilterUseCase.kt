package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoFilterRepository
import com.example.myfirstapp.core.model.TodoPriorityFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedTodoPriorityFilterUseCase @Inject constructor(
    private val repository: TodoFilterRepository
) {
    operator fun invoke(): Flow<TodoPriorityFilter> = repository.observeSelectedPriorityFilter()
}


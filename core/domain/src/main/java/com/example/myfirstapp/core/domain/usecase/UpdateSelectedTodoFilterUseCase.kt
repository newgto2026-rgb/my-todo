package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoFilterRepository
import com.example.myfirstapp.core.model.TodoFilter
import javax.inject.Inject

class UpdateSelectedTodoFilterUseCase @Inject constructor(
    private val repository: TodoFilterRepository
) {
    suspend operator fun invoke(filter: TodoFilter): Result<Unit> = repository.setSelectedFilter(filter)
}

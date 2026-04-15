package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoFilterRepository
import com.example.myfirstapp.core.model.TodoPriorityFilter
import javax.inject.Inject

class UpdateSelectedTodoPriorityFilterUseCase @Inject constructor(
    private val repository: TodoFilterRepository
) {
    suspend operator fun invoke(filter: TodoPriorityFilter): Result<Unit> =
        repository.setSelectedPriorityFilter(filter)
}

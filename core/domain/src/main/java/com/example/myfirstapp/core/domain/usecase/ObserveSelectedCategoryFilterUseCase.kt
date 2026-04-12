package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoFilterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedCategoryFilterUseCase @Inject constructor(
    private val repository: TodoFilterRepository
) {
    operator fun invoke(): Flow<Long?> = repository.observeSelectedCategoryFilter()
}

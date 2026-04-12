package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedCategoryFilterUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    operator fun invoke(): Flow<Long?> = repository.observeSelectedCategoryFilter()
}

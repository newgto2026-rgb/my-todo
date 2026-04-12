package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Long): Result<Unit> = repository.deleteCategory(id)
}

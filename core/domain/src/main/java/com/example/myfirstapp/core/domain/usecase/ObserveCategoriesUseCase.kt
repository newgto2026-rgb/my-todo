package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoCategoryRepository
import com.example.myfirstapp.core.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoriesUseCase @Inject constructor(
    private val repository: TodoCategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.observeCategories()
}

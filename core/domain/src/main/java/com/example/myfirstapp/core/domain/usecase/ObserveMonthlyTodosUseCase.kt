package com.example.myfirstapp.core.domain.usecase

import com.example.myfirstapp.core.domain.repository.TodoItemRepository
import com.example.myfirstapp.core.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

class ObserveMonthlyTodosUseCase @Inject constructor(
    private val repository: TodoItemRepository
) {
    operator fun invoke(yearMonth: YearMonth): Flow<List<TodoItem>> {
        val range = yearMonth.toDateRange()
        return repository.observeTodosByDueDateRange(
            startDate = range.startDate,
            endDate = range.endDate
        )
    }
}

data class MonthDateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

internal fun YearMonth.toDateRange(): MonthDateRange =
    MonthDateRange(
        startDate = atDay(1),
        endDate = atEndOfMonth()
    )

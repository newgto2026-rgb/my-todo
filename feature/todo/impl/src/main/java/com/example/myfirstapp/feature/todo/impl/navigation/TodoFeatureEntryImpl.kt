package com.example.myfirstapp.feature.todo.impl.navigation

import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.ui.navigation.AppNavigator
import com.example.myfirstapp.feature.todo.api.TodoAllRoute
import com.example.myfirstapp.feature.todo.api.TodoCompletedRoute
import com.example.myfirstapp.feature.todo.api.TodoEditRoute
import com.example.myfirstapp.feature.todo.api.TodoFeatureEntry
import com.example.myfirstapp.feature.todo.api.TodoTodayRoute
import com.example.myfirstapp.feature.todo.impl.ui.TodoListRoute
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject

class TodoFeatureEntryImpl @Inject constructor() : TodoFeatureEntry {
    override val route: NavKey = TodoAllRoute
    override val isStartDestination: Boolean = true

    override fun register(entryProviderScope: EntryProviderScope<NavKey>, navigator: AppNavigator) {
        entryProviderScope.entry<TodoAllRoute> {
            TodoListRoute(
                presetFilter = TodoFilter.ALL,
                onBackBlockedChange = navigator::setBackBlocked
            )
        }
        entryProviderScope.entry<TodoTodayRoute> {
            TodoListRoute(
                presetFilter = TodoFilter.TODAY,
                onBackBlockedChange = navigator::setBackBlocked
            )
        }
        entryProviderScope.entry<TodoCompletedRoute> {
            TodoListRoute(
                presetFilter = TodoFilter.COMPLETED,
                onBackBlockedChange = navigator::setBackBlocked
            )
        }
        entryProviderScope.entry<TodoEditRoute> { route ->
            TodoListRoute(
                presetFilter = TodoFilter.ALL,
                initialEditTodoId = route.todoId,
                isEditOnlyEntry = route.editOnly,
                onEditOnlyExit = {
                    if (route.editOnly) {
                        navigator.goBack()
                    }
                },
                onBackBlockedChange = navigator::setBackBlocked
            )
        }
    }
}

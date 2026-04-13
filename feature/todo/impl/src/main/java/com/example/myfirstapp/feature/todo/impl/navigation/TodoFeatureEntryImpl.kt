package com.example.myfirstapp.feature.todo.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.feature.todo.api.TodoAllRoute
import com.example.myfirstapp.feature.todo.api.TodoCompletedRoute
import com.example.myfirstapp.feature.todo.api.TodoEditRoute
import com.example.myfirstapp.feature.todo.api.TodoFeatureEntry
import com.example.myfirstapp.feature.todo.api.TodoTodayRoute
import com.example.myfirstapp.feature.todo.impl.ui.TodoListRoute
import javax.inject.Inject

class TodoFeatureEntryImpl @Inject constructor() : TodoFeatureEntry {
    override val route: String = requireNotNull(TodoAllRoute::class.qualifiedName)
    override val isStartDestination: Boolean = true

    override fun register(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<TodoAllRoute> {
            TodoListRoute(presetFilter = TodoFilter.ALL)
        }
        navGraphBuilder.composable<TodoTodayRoute> {
            TodoListRoute(presetFilter = TodoFilter.TODAY)
        }
        navGraphBuilder.composable<TodoCompletedRoute> {
            TodoListRoute(presetFilter = TodoFilter.COMPLETED)
        }
        navGraphBuilder.composable<TodoEditRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TodoEditRoute>()
            TodoListRoute(
                presetFilter = TodoFilter.ALL,
                initialEditTodoId = route.todoId
            )
        }
    }
}

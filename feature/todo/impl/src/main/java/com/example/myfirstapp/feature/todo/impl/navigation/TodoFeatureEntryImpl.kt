package com.example.myfirstapp.feature.todo.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myfirstapp.feature.todo.api.TodoFeatureEntry
import com.example.myfirstapp.feature.todo.api.TodoRoute
import com.example.myfirstapp.feature.todo.impl.ui.TodoListRoute
import javax.inject.Inject

class TodoFeatureEntryImpl @Inject constructor() : TodoFeatureEntry {
    override val route: String = requireNotNull(TodoRoute::class.qualifiedName)
    override val isStartDestination: Boolean = true

    override fun register(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<TodoRoute> {
            TodoListRoute()
        }
    }
}

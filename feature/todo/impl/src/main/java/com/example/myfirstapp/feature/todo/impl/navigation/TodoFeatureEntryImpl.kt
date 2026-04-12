package com.example.myfirstapp.feature.todo.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myfirstapp.core.ui.navigation.AppFeatureEntry
import com.example.myfirstapp.feature.todo.impl.ui.TodoListRoute
import javax.inject.Inject

class TodoFeatureEntryImpl @Inject constructor() : AppFeatureEntry {
    override val route: String = TODO_ROUTE

    override fun register(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable(route) {
            TodoListRoute()
        }
    }

    private companion object {
        const val TODO_ROUTE = "todo_route"
    }
}

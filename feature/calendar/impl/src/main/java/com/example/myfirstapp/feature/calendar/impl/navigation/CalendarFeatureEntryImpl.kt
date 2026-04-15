package com.example.myfirstapp.feature.calendar.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myfirstapp.feature.calendar.api.CalendarFeatureEntry
import com.example.myfirstapp.feature.calendar.api.CalendarRoute
import com.example.myfirstapp.feature.calendar.impl.ui.screen.CalendarRouteScreen
import com.example.myfirstapp.feature.todo.api.TodoEditRoute
import javax.inject.Inject

class CalendarFeatureEntryImpl @Inject constructor() : CalendarFeatureEntry {
    override val route: String = requireNotNull(CalendarRoute::class.qualifiedName)

    override fun register(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<CalendarRoute> {
            CalendarRouteScreen(
                onNavigateToTodoEdit = { todoId ->
                    navController.navigate(TodoEditRoute(todoId = todoId, editOnly = true))
                }
            )
        }
    }
}

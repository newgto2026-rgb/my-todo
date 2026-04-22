package com.example.myfirstapp.feature.calendar.impl.navigation

import com.example.myfirstapp.core.ui.navigation.AppNavigator
import com.example.myfirstapp.feature.calendar.api.CalendarFeatureEntry
import com.example.myfirstapp.feature.calendar.api.CalendarRoute
import com.example.myfirstapp.feature.calendar.impl.ui.screen.CalendarRouteScreen
import com.example.myfirstapp.feature.todo.api.TodoEditRoute
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import javax.inject.Inject

class CalendarFeatureEntryImpl @Inject constructor() : CalendarFeatureEntry {
    override val route: NavKey = CalendarRoute

    override fun register(entryProviderScope: EntryProviderScope<NavKey>, navigator: AppNavigator) {
        entryProviderScope.entry<CalendarRoute> {
            CalendarRouteScreen(
                onNavigateToTodoEdit = { todoId ->
                    navigator.navigate(TodoEditRoute(todoId = todoId, editOnly = true))
                }
            )
        }
    }
}

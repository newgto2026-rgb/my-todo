package com.example.myfirstapp.app

import androidx.annotation.StringRes
import com.example.myfirstapp.R
import com.example.myfirstapp.feature.todo.api.TodoAllRoute
import com.example.myfirstapp.feature.todo.api.TodoCompletedRoute
import com.example.myfirstapp.feature.todo.api.TodoTodayRoute

enum class AppTabDestination(
    val route: String,
    @StringRes val labelRes: Int
) {
    ALL(
        route = requireNotNull(TodoAllRoute::class.qualifiedName),
        labelRes = R.string.tab_all
    ),
    TODAY(
        route = requireNotNull(TodoTodayRoute::class.qualifiedName),
        labelRes = R.string.tab_today
    ),
    COMPLETED(
        route = requireNotNull(TodoCompletedRoute::class.qualifiedName),
        labelRes = R.string.tab_completed
    );

    companion object {
        val tabs: List<AppTabDestination> = entries

        fun fromRoute(route: String?): AppTabDestination? =
            tabs.firstOrNull { tab -> tab.route == route }
    }
}

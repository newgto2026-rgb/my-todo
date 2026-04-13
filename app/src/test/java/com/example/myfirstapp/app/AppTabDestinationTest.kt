package com.example.myfirstapp.app

import com.example.myfirstapp.feature.calendar.api.CalendarRoute
import com.example.myfirstapp.feature.todo.api.TodoAllRoute
import com.example.myfirstapp.feature.todo.api.TodoCompletedRoute
import com.example.myfirstapp.feature.todo.api.TodoTodayRoute
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AppTabDestinationTest {

    @Test
    fun tabs_includeCalendarRoute() {
        assertThat(AppTabDestination.tabs.map { it.route }).contains(
            requireNotNull(CalendarRoute::class.qualifiedName)
        )
    }

    @Test
    fun fromRoute_returnsExpectedTabs() {
        assertThat(AppTabDestination.fromRoute(requireNotNull(TodoAllRoute::class.qualifiedName)))
            .isEqualTo(AppTabDestination.ALL)
        assertThat(AppTabDestination.fromRoute(requireNotNull(TodoTodayRoute::class.qualifiedName)))
            .isEqualTo(AppTabDestination.TODAY)
        assertThat(AppTabDestination.fromRoute(requireNotNull(TodoCompletedRoute::class.qualifiedName)))
            .isEqualTo(AppTabDestination.COMPLETED)
        assertThat(AppTabDestination.fromRoute(requireNotNull(CalendarRoute::class.qualifiedName)))
            .isEqualTo(AppTabDestination.CALENDAR)
    }

    @Test
    fun tabs_haveUniqueRoutes() {
        val routes = AppTabDestination.tabs.map { it.route }
        assertThat(routes).containsNoDuplicates()
    }
}

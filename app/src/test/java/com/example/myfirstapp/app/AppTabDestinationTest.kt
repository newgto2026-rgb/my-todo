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
            CalendarRoute
        )
    }

    @Test
    fun fromRoute_returnsExpectedTabs() {
        assertThat(AppTabDestination.fromRoute(TodoAllRoute))
            .isEqualTo(AppTabDestination.ALL)
        assertThat(AppTabDestination.fromRoute(TodoTodayRoute))
            .isEqualTo(AppTabDestination.TODAY)
        assertThat(AppTabDestination.fromRoute(TodoCompletedRoute))
            .isEqualTo(AppTabDestination.COMPLETED)
        assertThat(AppTabDestination.fromRoute(CalendarRoute))
            .isEqualTo(AppTabDestination.CALENDAR)
    }

    @Test
    fun tabs_haveUniqueRoutes() {
        val routes = AppTabDestination.tabs.map { it.route }
        assertThat(routes).containsNoDuplicates()
    }
}

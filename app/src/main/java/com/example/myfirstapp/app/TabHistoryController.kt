package com.example.myfirstapp.app

class TabHistoryController(startRoute: String) {
    private val history = ArrayDeque<String>()
    private var currentRoute: String = startRoute

    fun syncCurrentRoute(route: String) {
        currentRoute = route
    }

    fun recordSelection(route: String) {
        if (currentRoute == route) return
        if (history.lastOrNull() != currentRoute) {
            history.addLast(currentRoute)
        }
        currentRoute = route
    }

    fun popPreviousOrNull(currentRoute: String): String? {
        this.currentRoute = currentRoute
        while (history.isNotEmpty()) {
            val candidate = history.removeLast()
            if (candidate != currentRoute) {
                this.currentRoute = candidate
                return candidate
            }
        }
        return null
    }
}

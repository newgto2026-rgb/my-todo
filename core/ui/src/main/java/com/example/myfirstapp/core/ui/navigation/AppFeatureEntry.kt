package com.example.myfirstapp.core.ui.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

interface AppFeatureEntry {
    val route: NavKey
    val isStartDestination: Boolean
        get() = false
    fun register(entryProviderScope: EntryProviderScope<NavKey>, navigator: AppNavigator)
}

interface AppNavigator {
    fun navigate(route: NavKey)
    fun goBack(): Boolean
    fun setBackBlocked(blocked: Boolean)
}

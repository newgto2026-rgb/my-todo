package com.example.myfirstapp.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.core.ui.navigation.AppFeatureEntry

@Composable
fun AppNavHost(entries: Set<@JvmSuppressWildcards AppFeatureEntry>) {
    val navController = rememberNavController()
    val sortedEntries = remember(entries) { entries.sortedBy(AppFeatureEntry::route) }
    val startDestination = sortedEntries
        .firstOrNull { it.isStartDestination }
        ?.route
        ?: sortedEntries.firstOrNull()?.route
        ?: return

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        sortedEntries.forEach { entry ->
            entry.register(this, navController)
        }
    }
}

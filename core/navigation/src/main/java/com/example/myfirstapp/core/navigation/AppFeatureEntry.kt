package com.example.myfirstapp.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface AppFeatureEntry {
    val route: String
    val isStartDestination: Boolean
        get() = false
    fun register(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}

package com.example.myfirstapp.core.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface AppFeatureEntry {
    val route: String
    fun register(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}

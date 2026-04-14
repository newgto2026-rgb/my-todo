package com.example.myfirstapp.app

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.core.navigation.AppFeatureEntry

@Composable
fun AppNavHost(entries: Set<@JvmSuppressWildcards AppFeatureEntry>) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val backPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val sortedEntries = remember(entries) { entries.sortedBy(AppFeatureEntry::route) }
    val startDestination = AppTabDestination.ALL.route
    val tabHistoryController = remember(startDestination) { TabHistoryController(startDestination) }
    var isHandlingBack by remember { mutableStateOf(false) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val currentTab = AppTabDestination.fromRoute(currentRoute)
    val isBackBlocked by (currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("app_back_blocked", false)
        ?.collectAsState() ?: remember { mutableStateOf(false) })

    LaunchedEffect(currentRoute) {
        if (currentRoute == null) return@LaunchedEffect
        if (isHandlingBack) {
            isHandlingBack = false
        }
        tabHistoryController.syncCurrentRoute(currentRoute)
    }

    BackHandler(enabled = currentTab != null && !isBackBlocked) {
        val route = currentTab?.route ?: return@BackHandler
        val previousRoute = tabHistoryController.popPreviousOrNull(route)
        val fallbackRoute = AppTabDestination.ALL.route
        val target = previousRoute ?: if (route != fallbackRoute) fallbackRoute else null

        if (target == null) {
            (context as? android.app.Activity)?.finish()
                ?: backPressedDispatcherOwner?.onBackPressedDispatcher?.onBackPressed()
            return@BackHandler
        }

        isHandlingBack = true
        navController.navigate(target) {
            launchSingleTop = true
            restoreState = true
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                selectedTab = currentTab,
                onTabSelected = { tab ->
                    if (tab == currentTab) return@AppBottomBar
                    tabHistoryController.recordSelection(tab.route)
                    navController.navigate(tab.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            sortedEntries.forEach { entry ->
                entry.register(this, navController)
            }
        }
    }
}

@Composable
private fun AppBottomBar(
    selectedTab: AppTabDestination?,
    onTabSelected: (AppTabDestination) -> Unit
) {
    NavigationBar {
        AppTabDestination.tabs.forEach { tab ->
            val icon = when (tab) {
                AppTabDestination.ALL -> Icons.Default.GridView
                AppTabDestination.TODAY -> Icons.Default.Today
                AppTabDestination.COMPLETED -> Icons.Default.CheckCircle
                AppTabDestination.CALENDAR -> Icons.Default.DateRange
            }
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(text = stringResource(tab.labelRes)) },
                modifier = Modifier.testTag("app_tab_${tab.name.lowercase()}")
            )
        }
    }
}

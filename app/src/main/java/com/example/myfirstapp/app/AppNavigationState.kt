package com.example.myfirstapp.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import kotlinx.serialization.builtins.ListSerializer

@Composable
fun rememberAppNavigationState(
    startRoute: NavKey,
    topLevelRoutes: Set<NavKey>
): AppNavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute,
        topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(startRoute)
    }
    val topLevelHistory = rememberSerializable(
        startRoute,
        topLevelRoutes,
        serializer = MutableStateSerializer(ListSerializer(NavKeySerializer()))
    ) {
        mutableStateOf(emptyList())
    }
    val backStacks = topLevelRoutes.associateWith { key -> rememberNavBackStack(key) }

    return remember(startRoute, topLevelRoutes) {
        AppNavigationState(
            startRoute = startRoute,
            topLevelRoutes = topLevelRoutes,
            topLevelRoute = topLevelRoute,
            topLevelHistory = topLevelHistory,
            backStacks = backStacks
        )
    }
}

class AppNavigationState(
    val startRoute: NavKey,
    val topLevelRoutes: Set<NavKey>,
    topLevelRoute: MutableState<NavKey>,
    topLevelHistory: MutableState<List<NavKey>>,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>
) {
    var topLevelRoute: NavKey by topLevelRoute
    var topLevelHistory: List<NavKey> by topLevelHistory

    val currentRoute: NavKey
        get() = currentStack.last()

    val currentStack: NavBackStack<NavKey>
        get() = requireNotNull(backStacks[topLevelRoute]) {
            "Stack for $topLevelRoute not found"
        }

    val stacksInUse: List<NavKey>
        get() = (topLevelHistory + topLevelRoute)
            .distinct()
            .ifEmpty { listOf(startRoute) }
}

@Composable
fun AppNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
        rememberViewModelStoreNavEntryDecorator<NavKey>()
    )
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { route -> decoratedEntries[route] ?: emptyList() }
        .toMutableStateList()
}

package com.example.myfirstapp.feature.todo.api

import com.example.myfirstapp.core.ui.navigation.AppFeatureEntry
import kotlinx.serialization.Serializable

@Serializable
data object TodoAllRoute

@Serializable
data object TodoTodayRoute

@Serializable
data object TodoCompletedRoute

@Serializable
data class TodoEditRoute(val todoId: Long)

interface TodoFeatureEntry : AppFeatureEntry

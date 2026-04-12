package com.example.myfirstapp.feature.todo.api

import com.example.myfirstapp.core.ui.navigation.AppFeatureEntry

interface TodoFeatureEntry : AppFeatureEntry {
    companion object {
        const val ROUTE = "todo_route"
    }
}

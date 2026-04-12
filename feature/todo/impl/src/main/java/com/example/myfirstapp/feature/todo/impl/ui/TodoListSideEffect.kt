package com.example.myfirstapp.feature.todo.impl.ui

sealed interface TodoListSideEffect {
    data class ShowSnackbar(val message: String) : TodoListSideEffect
}

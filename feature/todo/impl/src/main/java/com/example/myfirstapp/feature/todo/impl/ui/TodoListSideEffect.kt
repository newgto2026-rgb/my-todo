package com.example.myfirstapp.feature.todo.impl.ui

import androidx.annotation.StringRes

sealed interface TodoListSideEffect {
    data class ShowSnackbar(@StringRes val messageRes: Int) : TodoListSideEffect
}

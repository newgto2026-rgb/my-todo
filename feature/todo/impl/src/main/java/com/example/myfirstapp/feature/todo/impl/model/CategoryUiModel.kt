package com.example.myfirstapp.feature.todo.impl.model

import androidx.compose.runtime.Immutable

@Immutable
data class CategoryUiModel(
    val id: Long?,
    val name: String,
    val colorHex: String?
)

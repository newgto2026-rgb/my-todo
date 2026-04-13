package com.example.myfirstapp.feature.todo.impl.ui

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

internal fun parseHexOrNull(value: String?): Color? {
    if (value.isNullOrBlank()) return null
    return runCatching { Color(value.toColorInt()) }.getOrNull()
}

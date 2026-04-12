package com.example.myfirstapp.feature.todo.impl.ui

import android.graphics.Color.parseColor
import androidx.compose.ui.graphics.Color

internal fun parseHexOrNull(value: String?): Color? {
    if (value.isNullOrBlank()) return null
    return runCatching { Color(parseColor(value)) }.getOrNull()
}

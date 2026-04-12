package com.example.myfirstapp.feature.todo.impl.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.core.model.TodoFilter

@Composable
internal fun EmptyState(
    modifier: Modifier = Modifier,
    message: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawDashedBorder(
                strokeWidth = 1.dp.value,
                color = Color(0xFFD9DCE7),
                cornerRadius = 16.dp.value,
                on = 10.dp.value,
                off = 8.dp.value
            )
            .padding(vertical = 42.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "✧", color = Color(0xFFC9CDD8), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF6F7481),
            textAlign = TextAlign.Center
        )
    }
}

internal fun emptyMessage(filter: TodoFilter): String = when (filter) {
    TodoFilter.ALL -> "No tasks yet.\nTap + to add your first task."
    TodoFilter.TODAY -> "No tasks due today."
    TodoFilter.COMPLETED -> "No completed tasks yet."
}

private fun Modifier.drawDashedBorder(
    strokeWidth: Float,
    color: Color,
    cornerRadius: Float,
    on: Float,
    off: Float
): Modifier = this.then(
    Modifier
        .border(
            width = 1.dp,
            color = Color.Transparent,
            shape = RoundedCornerShape(cornerRadius.dp)
        )
        .drawBehind {
            drawRoundRect(
                color = color,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(on, off), 0f)
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
            )
        }
)

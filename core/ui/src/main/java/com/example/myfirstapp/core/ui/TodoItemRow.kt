package com.example.myfirstapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Composable
fun TodoItemRow(
    title: String,
    dueDateText: String?,
    reminderText: String?,
    isDone: Boolean,
    isEmphasized: Boolean,
    isReminderEnabled: Boolean,
    onToggleDone: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    categoryName: String? = null,
    categoryColorHex: String? = null
) {
    val containerColor = when {
        isDone -> Color(0xFFF1F2F8)
        isEmphasized -> Color(0xFFE8EBF4)
        else -> Color.White
    }
    val subtitleColor = when {
        isDone -> Color(0xFFB2B7C4)
        isEmphasized -> Color(0xFF4A6697)
        else -> Color(0xFF7F8491)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isDone) Color(0xFF7388B3) else Color.Transparent)
                .border(width = 2.dp, color = Color(0xFFB8C3D9), shape = CircleShape)
                .clickable(onClick = onToggleDone),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isDone) Color(0xFF9AA0AF) else Color(0xFF2F3441),
                    textDecoration = if (isDone) TextDecoration.LineThrough else null,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!categoryName.isNullOrBlank()) {
                    Spacer(Modifier.size(8.dp))
                    CategoryBadge(
                        name = categoryName,
                        color = parseHexOrNull(categoryColorHex) ?: Color(0xFF8D95A8)
                    )
                }
            }
            if (dueDateText != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isDone && !isEmphasized) {
                        CalendarDot()
                    }
                    Text(
                        text = dueDateText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtitleColor,
                        modifier = Modifier.padding(start = if (!isDone && !isEmphasized) 4.dp else 0.dp)
                    )
                    if (isReminderEnabled && !reminderText.isNullOrBlank()) {
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodyMedium,
                            color = subtitleColor
                        )
                        Spacer(Modifier.size(6.dp))
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = subtitleColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            text = reminderText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = subtitleColor,
                        )
                    }
                }
            } else if (isReminderEnabled && !reminderText.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = subtitleColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = reminderText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = subtitleColor
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryBadge(
    name: String,
    color: Color
) {
    val background = color.copy(alpha = 0.16f)
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = background
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

private fun parseHexOrNull(value: String?): Color? {
    if (value.isNullOrBlank()) return null
    return runCatching { Color(value.toColorInt()) }.getOrNull()
}

@Composable
private fun CalendarDot() {
    Box(
        modifier = Modifier
            .size(12.dp)
            .border(width = 1.dp, color = Color(0xFFB5BBC8), shape = RoundedCornerShape(3.dp))
            .padding(1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFB5BBC8), shape = RoundedCornerShape(2.dp))
        )
    }
}

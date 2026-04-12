package com.example.myfirstapp.feature.todo.impl.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.feature.todo.impl.R

@Composable
internal fun TodoEditorReminderSection(
    reminderEnabled: Boolean,
    reminderDateTimeInput: String,
    reminderRepeatType: ReminderRepeatType,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderDatePickerClick: () -> Unit,
    onReminderRepeatTypeChange: (ReminderRepeatType) -> Unit
) {
    Text(
        text = stringResource(R.string.todo_editor_reminder_label),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF7A7F8C)
    )
    Spacer(Modifier.height(10.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF0F1F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.todo_editor_enable_reminder),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = onReminderEnabledChange
                )
            }
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        enabled = reminderEnabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onReminderDatePickerClick() },
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFE7E9EE)
            ) {
                Text(
                    text = if (reminderDateTimeInput.isBlank()) {
                        stringResource(R.string.todo_editor_select_reminder_datetime)
                    } else {
                        reminderDateTimeInput
                    },
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (reminderDateTimeInput.isBlank()) Color(0xFFB0B5C2) else Color(0xFF2F3441)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.todo_editor_repeat_label),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF7A7F8C)
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TodoReminderRepeatOption.values().forEach { option ->
                    SelectableCategoryChip(
                        label = stringResource(option.labelRes),
                        selected = reminderRepeatType == option.type,
                        colorHex = null,
                        enabled = reminderEnabled,
                        onClick = { onReminderRepeatTypeChange(option.type) }
                    )
                }
            }
        }
    }
}

private enum class TodoReminderRepeatOption(
    val type: ReminderRepeatType,
    @StringRes val labelRes: Int
) {
    NONE(ReminderRepeatType.NONE, R.string.todo_editor_repeat_none),
    DAILY(ReminderRepeatType.DAILY, R.string.todo_editor_repeat_daily),
    WEEKLY(ReminderRepeatType.WEEKLY, R.string.todo_editor_repeat_weekly)
}

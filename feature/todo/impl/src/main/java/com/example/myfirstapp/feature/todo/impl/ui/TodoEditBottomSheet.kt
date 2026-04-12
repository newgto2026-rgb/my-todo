package com.example.myfirstapp.feature.todo.impl.ui

import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.core.model.ReminderRepeatType
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneId.systemDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTodoBottomSheet(
    title: String,
    dueDateInput: String,
    reminderEnabled: Boolean,
    reminderDateTimeInput: String,
    reminderRepeatType: ReminderRepeatType,
    categories: List<CategoryUiModel>,
    selectedCategoryId: Long?,
    errorMessage: String?,
    onTitleChange: (String) -> Unit,
    onDateInputChange: (String) -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderDateTimeInputChange: (String) -> Unit,
    onReminderRepeatTypeChange: (ReminderRepeatType) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onManageCategoriesClick: () -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    showDelete: Boolean
) {
    val focusManager = LocalFocusManager.current
    var hasFocusedInput by remember { mutableStateOf(false) }
    BackHandler(enabled = true) {
        if (hasFocusedInput) {
            focusManager.clearFocus(force = true)
        } else {
            onDismiss()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showReminderDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue -> newValue != SheetValue.Hidden }
    )
    val context = LocalContext.current
    val now = remember { LocalDateTime.now() }
    val parsedReminder = remember(reminderDateTimeInput) { parseReminderInput(reminderDateTimeInput) }
    var pendingReminderDate by remember { mutableStateOf(parsedReminder?.toLocalDate()) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = isoDateToUtcMillis(dueDateInput)
    )
    val reminderDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = (parsedReminder ?: now)
            .atZone(systemDefault())
            .toInstant()
            .toEpochMilli()
    )

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {},
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        containerColor = Color(0xFFF6F7FB),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = 42.dp, height = 5.dp)
                    .background(Color(0xFFD4D7E0), RoundedCornerShape(12.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "New Task",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF323640)
                )
                Spacer(Modifier.weight(1f))
                Surface(shape = CircleShape, color = Color(0xFFEAECF3), onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp),
                        tint = Color(0xFF5C6170)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text(
                text = "TASK DESCRIPTION",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF7A7F8C)
            )
            Spacer(Modifier.height(10.dp))
            TextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("What needs to be done?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .testTag("task_title_input")
                    .onFocusChanged { state ->
                        hasFocusedInput = state.hasFocus || state.isCaptured
                    },
                singleLine = false,
                shape = RoundedCornerShape(14.dp),
                isError = errorMessage != null,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEBEDF4),
                    unfocusedContainerColor = Color(0xFFEBEDF4),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(12.dp))
            Surface(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("due_date_selector"),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFEBEDF4)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF5C6170)
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = if (dueDateInput.isBlank()) "Select due date" else dueDateInput,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (dueDateInput.isBlank()) Color(0xFF8E94A3) else Color(0xFF2F3441)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            TodoEditorCategorySection(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected,
                onManageCategoriesClick = onManageCategoriesClick
            )

            Spacer(Modifier.height(14.dp))
            TodoEditorReminderSection(
                reminderEnabled = reminderEnabled,
                reminderDateTimeInput = reminderDateTimeInput,
                reminderRepeatType = reminderRepeatType,
                onReminderEnabledChange = onReminderEnabledChange,
                onReminderDatePickerClick = { showReminderDatePicker = true },
                onReminderRepeatTypeChange = onReminderRepeatTypeChange
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            Spacer(Modifier.height(18.dp))
            Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                if (showDelete) {
                    TextButton(onClick = onDelete) { Text("Delete") }
                }
                Button(
                    onClick = onSave,
                    enabled = title.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A6697),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFB9C3D8),
                        disabledContentColor = Color(0xFFE9EDF7)
                    )
                ) {
                    Text("Save")
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateInputChange(utcMillisToIsoDate(datePickerState.selectedDateMillis))
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showReminderDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showReminderDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val pickedMillis = reminderDatePickerState.selectedDateMillis
                        pendingReminderDate = pickedMillis?.let {
                            Instant.ofEpochMilli(it).atZone(systemDefault()).toLocalDate()
                        }
                        showReminderDatePicker = false

                        val baseTime = parsedReminder ?: now
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val date = pendingReminderDate ?: LocalDate.now()
                                val selected = LocalDateTime.of(date.year, date.month, date.dayOfMonth, hour, minute)
                                onReminderDateTimeInputChange(selected.format(REMINDER_FORMATTER))
                            },
                            baseTime.hour,
                            baseTime.minute,
                            true
                        ).show()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showReminderDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = reminderDatePickerState)
        }
    }
}

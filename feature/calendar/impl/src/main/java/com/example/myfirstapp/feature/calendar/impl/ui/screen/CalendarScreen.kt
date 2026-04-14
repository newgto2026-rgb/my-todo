package com.example.myfirstapp.feature.calendar.impl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.myfirstapp.feature.calendar.impl.R
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarAction
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarDayUiModel
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarSelectedTodoUiModel
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarSideEffect
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarUiState
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarViewModel
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarRouteScreen(
    onNavigateToTodoEdit: (Long) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is CalendarSideEffect.NavigateToTodoEdit -> {
                    onNavigateToTodoEdit(sideEffect.todoId)
                }
            }
        }
    }

    CalendarScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
private fun CalendarScreen(
    uiState: CalendarUiState,
    onAction: (CalendarAction) -> Unit
) {
    val locale = Locale.getDefault()
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM", locale)
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", locale)
    val selectedDateLabel = uiState.selectedDate.format(DateTimeFormatter.ofPattern("yyyy MMM d", locale))
    val selectedDateCount = uiState.selectedDateTodos.size

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 30.dp, top = 16.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.testTag("calendar_prev_month"),
                    onClick = { onAction(CalendarAction.OnPreviousMonthClick) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.calendar_month_navigation_previous)
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("calendar_month_label")
                ) {
                    Text(
                        text = uiState.currentMonth.format(monthFormatter),
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                        color = Color(0xFF5044E3)
                    )
                    Text(
                        text = uiState.currentMonth.format(yearFormatter),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light),
                        color = Color(0xFF5A6065)
                    )
                }
                IconButton(
                    modifier = Modifier.testTag("calendar_next_month"),
                    onClick = { onAction(CalendarAction.OnNextMonthClick) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.calendar_month_navigation_next)
                    )
                }
            }

            Text(
                text = pluralStringResource(
                    id = R.plurals.calendar_a11y_todo_count,
                    count = selectedDateCount,
                    selectedDateCount
                ),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF5A6065),
                modifier = Modifier.padding(start = 2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.82f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
                    WeekdayHeaderRow()
                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.days.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            week.forEach { day ->
                                CalendarDayCell(
                                    day = day,
                                    onClick = { date -> onAction(CalendarAction.OnDateClick(date)) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.calendar_bottom_sheet_title, selectedDateLabel),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.testTag("calendar_day_todo_list_title")
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFD8E2FF)
                ) {
                    Text(
                        text = selectedDateCount.toString(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF45526D)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.selectedDateTodos.isEmpty()) {
                Text(
                    text = stringResource(R.string.calendar_bottom_sheet_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .testTag("calendar_day_todo_list_empty")
                        .padding(bottom = 20.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = uiState.selectedDateTodos, key = { it.id }) { todo ->
                        DayTodoItem(
                            todo = todo,
                            onClick = { onAction(CalendarAction.OnTodoClick(todo.id)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeaderRow() {
    val locale = Locale.getDefault()
    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
    val dayOrder = List(7) { firstDayOfWeek.plus(it.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        dayOrder.forEach { dayOfWeek ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF757B81)
                )
            }
        }
    }
}

@Composable
private fun RowScope.CalendarDayCell(
    day: CalendarDayUiModel,
    onClick: (java.time.LocalDate) -> Unit
) {
    if (day.date == null) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        return
    }

    val totalCount = day.indicatorCount + day.overflowCount
    val isInactiveMonth = !day.isCurrentMonth
    val textColor = when {
        day.isSelected -> Color.White
        isInactiveMonth -> Color(0xFF5A6065).copy(alpha = 0.24f)
        else -> Color(0xFF2D3338)
    }

    val a11yParts = buildList {
        add(
            day.date.format(
                DateTimeFormatter.ofPattern("yyyy MMMM d EEEE", Locale.getDefault())
            )
        )
        add(
            pluralStringResource(
                id = R.plurals.calendar_a11y_todo_count,
                count = totalCount,
                totalCount
            )
        )
        if (day.isSelected) add(stringResource(R.string.calendar_a11y_selected))
        if (day.isToday) add(stringResource(R.string.calendar_a11y_today))
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .testTag("calendar_day_${day.date}")
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    day.isSelected -> Color.Transparent
                    day.isToday && !isInactiveMonth -> Color(0xFFEFF2F8)
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = !isInactiveMonth) { onClick(day.date) }
            .semantics { contentDescription = a11yParts.joinToString(separator = ", ") }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier.size(34.dp),
            contentAlignment = Alignment.Center
        ) {
            if (day.isSelected) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF5044E3), Color(0xFF6C63FF))
                            ),
                            shape = CircleShape
                        )
                )
            }
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (!isInactiveMonth && day.indicatorCount > 0) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        color = if (day.isSelected) Color.White else Color(0xFF6D5586),
                        shape = CircleShape
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp).alpha(0f))
        }

        if (!isInactiveMonth && day.overflowCount > 0) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "+${day.overflowCount}",
                style = MaterialTheme.typography.labelSmall,
                color = if (day.isSelected) Color.White else Color(0xFF5A6065),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(AbsoluteRoundedCornerShape(8.dp))
                    .padding(horizontal = 2.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp).alpha(0f))
        }

        if (day.isSelected && day.indicatorCount > 0) {
            Spacer(modifier = Modifier.height(1.dp))
        }
        if (totalCount == 0) {
            Spacer(modifier = Modifier.height(1.dp).alpha(0f))
        }
    }
}

@Composable
private fun DayTodoItem(
    todo: CalendarSelectedTodoUiModel,
    onClick: () -> Unit
) {
    val dueText = todo.dueTimeLabel ?: stringResource(R.string.calendar_bottom_sheet_all_day)
    val reminderText = when (todo.reminderLeadMinutes) {
        0 -> stringResource(R.string.calendar_reminder_lead_at_time)
        5 -> stringResource(R.string.calendar_reminder_lead_5m)
        10 -> stringResource(R.string.calendar_reminder_lead_10m)
        30 -> stringResource(R.string.calendar_reminder_lead_30m)
        60 -> stringResource(R.string.calendar_reminder_lead_60m)
        else -> null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_day_todo_item_${todo.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (todo.isDone) Color(0xFFD5DBE1) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (todo.isDone) Color(0xFF6C63FF) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (todo.isDone) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = Color(0xFF6C63FF),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = if (todo.isDone) Color(0xFF2D3338).copy(alpha = 0.5f) else Color(0xFF2D3338)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5A6065)
                    )
                    Text(
                        text = dueText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5A6065)
                    )
                    if (todo.isReminderEnabled && !reminderText.isNullOrBlank()) {
                        Text(
                            text = "·",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5A6065)
                        )
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFF5A6065),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = reminderText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5A6065)
                        )
                    }
                }
            }
            Text(
                text = if (todo.isDone) {
                    stringResource(R.string.calendar_bottom_sheet_done)
                } else {
                    stringResource(R.string.calendar_bottom_sheet_pending)
                },
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (todo.isDone) Color(0xFF6C63FF) else Color(0xFF5A6065)
            )
        }
    }
}

private fun DayOfWeek.plus(days: Long): DayOfWeek {
    val normalized = ((value - 1 + days) % 7 + 7) % 7
    return DayOfWeek.of(normalized.toInt() + 1)
}

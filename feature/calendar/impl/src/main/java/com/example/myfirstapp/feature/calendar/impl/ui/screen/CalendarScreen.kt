package com.example.myfirstapp.feature.calendar.impl.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = uiState.currentMonth.format(monthFormatter),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.testTag("calendar_month_label")
                )
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

            Spacer(modifier = Modifier.height(10.dp))
            WeekdayHeaderRow()
            Spacer(modifier = Modifier.height(8.dp))

            uiState.days.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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

    if (uiState.isDayTodoSheetVisible) {
        DayTodoBottomSheet(
            selectedDate = uiState.selectedDate,
            todos = uiState.selectedDateTodos,
            onTodoClick = { onAction(CalendarAction.OnTodoClick(it)) },
            onDismiss = { onAction(CalendarAction.OnBottomSheetDismiss) }
        )
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
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    if (day.date == null || !day.isCurrentMonth) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        return
    }

    val totalCount = day.indicatorCount + day.overflowCount
    val colors = MaterialTheme.colorScheme
    val bgColor = if (day.isSelected) colors.primary else colors.surfaceVariant.copy(alpha = 0.45f)
    val textColor = if (day.isSelected) colors.onPrimary else colors.onSurface
    val borderColor = if (day.isToday) colors.primary else colors.outline.copy(alpha = 0.45f)

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
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick(day.date) }
            .semantics { contentDescription = a11yParts.joinToString(separator = ", ") }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = textColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (day.indicatorCount > 0) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                repeat(day.indicatorCount) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                color = if (day.isSelected) colors.onPrimary else colors.primary,
                                shape = CircleShape
                            )
                    )
                    if (it != day.indicatorCount - 1) {
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
            }
        }

        if (day.overflowCount > 0) {
            Text(
                text = "+${day.overflowCount}",
                style = MaterialTheme.typography.labelSmall,
                color = if (day.isSelected) colors.onPrimary else colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (totalCount == 0) {
            Spacer(modifier = Modifier.height(8.dp).alpha(0f))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DayTodoBottomSheet(
    selectedDate: java.time.LocalDate,
    todos: List<CalendarSelectedTodoUiModel>,
    onTodoClick: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val locale = Locale.getDefault()
    val dateLabel = selectedDate.format(DateTimeFormatter.ofPattern("yyyy MMM d", locale))

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("calendar_day_todo_sheet")
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.calendar_bottom_sheet_title, dateLabel),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (todos.isEmpty()) {
                Text(
                    text = stringResource(R.string.calendar_bottom_sheet_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .testTag("calendar_day_todo_sheet_empty")
                        .padding(bottom = 20.dp)
                )
                return@Column
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = todos, key = { it.id }) { todo ->
                    DayTodoItem(
                        todo = todo,
                        onClick = { onTodoClick(todo.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun DayTodoItem(
    todo: CalendarSelectedTodoUiModel,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_day_todo_item_${todo.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (todo.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (todo.isDone) colors.primary else colors.onSurfaceVariant
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onSurface
                )
                Text(
                    text = todo.reminderTimeLabel ?: stringResource(R.string.calendar_bottom_sheet_all_day),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
            Text(
                text = if (todo.isDone) {
                    stringResource(R.string.calendar_bottom_sheet_done)
                } else {
                    stringResource(R.string.calendar_bottom_sheet_pending)
                },
                style = MaterialTheme.typography.labelMedium,
                color = if (todo.isDone) colors.primary else colors.onSurfaceVariant
            )
        }
    }
}

private fun DayOfWeek.plus(days: Long): DayOfWeek {
    val normalized = ((value - 1 + days) % 7 + 7) % 7
    return DayOfWeek.of(normalized.toInt() + 1)
}

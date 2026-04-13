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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfirstapp.feature.calendar.impl.R
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarAction
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarDayUiModel
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarUiState
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarViewModel
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

@Composable
fun CalendarRouteScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
                    onClick = { onAction(CalendarAction.OnPreviousMonthClick) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.calendar_month_navigation_previous)
                    )
                }
                Text(
                    text = uiState.currentMonth.format(monthFormatter),
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(
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

private fun DayOfWeek.plus(days: Long): DayOfWeek {
    val normalized = ((value - 1 + days) % 7 + 7) % 7
    return DayOfWeek.of(normalized.toInt() + 1)
}

package com.example.myfirstapp.feature.calendar.impl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.feature.calendar.impl.R
import com.example.myfirstapp.feature.calendar.impl.ui.CalendarDayUiModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

private val HeaderGradient = listOf(Color(0xFF5044E3), Color(0xFF6C63FF))

@Composable
internal fun CalendarMonthGrid(
    days: List<CalendarDayUiModel>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        WeekdayHeaderRow()
        Spacer(modifier = Modifier.height(8.dp))

        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { day ->
                    CalendarDayCell(
                        day = day,
                        onClick = onDateClick
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
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
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale).uppercase(locale),
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
    onClick: (LocalDate) -> Unit
) {
    val date = day.date ?: run {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        return
    }

    val totalCount = day.indicatorCount + day.overflowCount
    val isInactiveMonth = !day.isCurrentMonth
    val hasItems = !isInactiveMonth && totalCount > 0
    val textColor = when {
        day.isSelected -> Color.White
        isInactiveMonth -> Color(0xFF5A6065).copy(alpha = 0.2f)
        hasItems -> Color(0xFF2D3338)
        else -> Color(0xFF2D3338).copy(alpha = 0.62f)
    }
    val dateFontWeight = if (hasItems || day.isSelected) FontWeight.SemiBold else FontWeight.Medium

    val a11yParts = buildList {
        add(date.format(DateTimeFormatter.ofPattern("yyyy MMMM d EEEE", Locale.getDefault())))
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
            .testTag("calendar_day_$date")
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = day.isCurrentMonth) { onClick(date) }
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
                            brush = Brush.linearGradient(colors = HeaderGradient),
                            shape = CircleShape
                        )
                )
            }
            if (!day.isSelected && hasItems) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = Color(0xFFDDE4FF),
                            shape = CircleShape
                        )
                )
            }
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = dateFontWeight),
                color = textColor
            )
        }
    }
}

private fun DayOfWeek.plus(days: Long): DayOfWeek {
    val normalized = ((value - 1 + days) % 7 + 7) % 7
    return DayOfWeek.of(normalized.toInt() + 1)
}

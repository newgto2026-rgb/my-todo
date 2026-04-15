package com.example.myfirstapp.feature.calendar.impl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.feature.calendar.impl.R
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun CalendarTopHeader(
    currentMonth: YearMonth,
    todayCount: Int,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    val locale = Locale.getDefault()
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM", locale)
    val yearFormatter = DateTimeFormatter.ofPattern("yyyy", locale)
    val monthLabel = "${currentMonth.format(monthFormatter)} ${currentMonth.format(yearFormatter)}"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .testTag("calendar_month_label")
                    .semantics { contentDescription = monthLabel }
            ) {
                Text(
                    text = currentMonth.format(monthFormatter),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                    color = Color(0xFF5044E3)
                )
                Text(
                    text = currentMonth.format(yearFormatter),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light),
                    color = Color(0xFF5A6065)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = pluralStringResource(
                    id = R.plurals.calendar_header_today_task_count,
                    count = todayCount,
                    todayCount
                ),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF5A6065)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.7f)
            ) {
                IconButton(
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("calendar_prev_month"),
                    onClick = onPreviousMonthClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.calendar_month_navigation_previous),
                        tint = Color(0xFF5A6065)
                    )
                }
            }
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.7f)
            ) {
                IconButton(
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("calendar_next_month"),
                    onClick = onNextMonthClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.calendar_month_navigation_next),
                        tint = Color(0xFF5A6065)
                    )
                }
            }
        }
    }
}

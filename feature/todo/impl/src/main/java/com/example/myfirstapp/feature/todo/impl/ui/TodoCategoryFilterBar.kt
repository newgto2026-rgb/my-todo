package com.example.myfirstapp.feature.todo.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import com.example.myfirstapp.core.model.TodoCategoryFilter
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
import androidx.compose.ui.res.stringResource
import com.example.myfirstapp.feature.todo.impl.R
import kotlin.math.max

@Composable
internal fun CategoryFilterBar(
    categories: List<CategoryUiModel>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val chipsPerRow = max(2, screenWidthDp / 110)
    val chips = buildList {
        add(
            CategoryChipData(
                label = stringResource(R.string.todo_category_all),
                selected = selectedCategoryId == null,
                color = Color(0xFF5F78A6),
                onClick = { onCategorySelected(null) }
            )
        )
        add(
            CategoryChipData(
                label = stringResource(R.string.todo_category_uncategorized),
                selected = selectedCategoryId == TodoCategoryFilter.UNCATEGORIZED_FILTER_ID,
                color = Color(0xFF8D95A8),
                onClick = { onCategorySelected(TodoCategoryFilter.UNCATEGORIZED_FILTER_ID) }
            )
        )
        categories.forEach { category ->
            add(
                CategoryChipData(
                    label = category.name,
                    selected = selectedCategoryId == category.id,
                    color = parseHexOrNull(category.colorHex) ?: Color(0xFF8A8F9D),
                    onClick = { onCategorySelected(category.id) }
                )
            )
        }
    }
    val rows = chips.chunked(chipsPerRow)
    val collapsedRows = rows.take(3)
    val visibleRows = if (expanded) rows else collapsedRows
    val showToggle = rows.size > 3

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        visibleRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { chip ->
                    CategoryFilterChip(
                        label = chip.label,
                        selected = chip.selected,
                        color = chip.color,
                        onClick = chip.onClick
                    )
                }
            }
        }
        if (showToggle) {
            CategoryFilterChip(
                label = if (expanded) {
                    stringResource(R.string.todo_toggle_less)
                } else {
                    stringResource(R.string.todo_toggle_more)
                },
                selected = false,
                color = Color(0xFF8D95A8),
                onClick = { expanded = !expanded }
            )
        }
    }
}

private data class CategoryChipData(
    val label: String,
    val selected: Boolean,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun CategoryFilterChip(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected) color.copy(alpha = 0.2f) else Color(0xFFE6E9F2),
        onClick = onClick
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (selected) color else Color(0xFF6C7382),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

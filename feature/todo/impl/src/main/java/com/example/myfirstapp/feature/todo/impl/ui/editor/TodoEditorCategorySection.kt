package com.example.myfirstapp.feature.todo.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
import com.example.myfirstapp.feature.todo.impl.R
import kotlin.math.max

@Composable
internal fun TodoEditorCategorySection(
    categories: List<CategoryUiModel>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    onManageCategoriesClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.todo_editor_category_label),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF7A7F8C)
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onManageCategoriesClick) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF5F78A6))
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.todo_editor_manage),
                color = Color(0xFF5F78A6),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }

    var expandedCategoryChips by rememberSaveable { mutableStateOf(false) }
    val containerWidthDp = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp().value
    }
    val chipsPerRow = max(2, (containerWidthDp / 120f).toInt())
    val chips = buildList {
        add(
            EditorCategoryChipData(
                label = stringResource(R.string.todo_category_uncategorized),
                selected = selectedCategoryId == null,
                colorHex = null,
                onClick = { onCategorySelected(null) }
            )
        )
        categories.forEach { category ->
            add(
                EditorCategoryChipData(
                    label = category.name,
                    selected = selectedCategoryId == category.id,
                    colorHex = category.colorHex,
                    onClick = { onCategorySelected(category.id) }
                )
            )
        }
    }
    val rows = chips.chunked(chipsPerRow)
    val collapsedRows = rows.take(2)
    val visibleRows = if (expandedCategoryChips) rows else collapsedRows
    val showToggle = rows.size > 2

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        visibleRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { chip ->
                    EditorCategoryChip(chip)
                }
            }
        }
        if (showToggle) {
            SelectableCategoryChip(
                label = if (expandedCategoryChips) {
                    stringResource(R.string.todo_toggle_less)
                } else {
                    stringResource(R.string.todo_toggle_more)
                },
                selected = false,
                colorHex = null,
                onClick = { expandedCategoryChips = !expandedCategoryChips }
            )
        }
    }
}

private data class EditorCategoryChipData(
    val label: String,
    val selected: Boolean,
    val colorHex: String?,
    val onClick: () -> Unit
)

@Composable
private fun EditorCategoryChip(chip: EditorCategoryChipData) {
    SelectableCategoryChip(
        label = chip.label,
        selected = chip.selected,
        colorHex = chip.colorHex,
        onClick = chip.onClick
    )
}

@Composable
internal fun SelectableCategoryChip(
    label: String,
    selected: Boolean,
    colorHex: String?,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val accent = parseHexOrNull(colorHex) ?: Color(0xFF5F78A6)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = when {
            !enabled -> Color(0xFFE9EBF1)
            selected -> accent.copy(alpha = 0.2f)
            else -> Color(0xFFE6E9F2)
        },
        onClick = if (enabled) onClick else ({})
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = when {
                !enabled -> Color(0xFFA8ADBA)
                selected -> accent
                else -> Color(0xFF6C7382)
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}

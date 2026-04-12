package com.example.myfirstapp.feature.todo.impl.ui

import android.graphics.Color.parseColor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.feature.todo.impl.model.CategoryUiModel
import com.example.myfirstapp.feature.todo.impl.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CategoryManagerBottomSheet(
    categories: List<CategoryUiModel>,
    categoryNameInput: String,
    categoryColorInput: String,
    categoryIconInput: String,
    editingCategoryId: Long?,
    onCategoryNameInputChange: (String) -> Unit,
    onCategoryColorInputChange: (String) -> Unit,
    onCategoryIconInputChange: (String) -> Unit,
    onCategoryEditClick: (Long) -> Unit,
    onCategoryDeleteClick: (Long) -> Unit,
    onSaveCategoryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var focusedInputCount by remember { mutableIntStateOf(0) }
    BackHandler(enabled = true) {
        if (focusedInputCount > 0) {
            focusManager.clearFocus(force = true)
        } else {
            onDismiss()
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue -> newValue != SheetValue.Hidden }
    )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {},
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.todo_category_manager_title),
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = categoryNameInput,
                onValueChange = onCategoryNameInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        focusedInputCount = if (state.hasFocus || state.isCaptured) 1 else 0
                    },
                label = { Text(stringResource(R.string.todo_category_manager_name)) },
                singleLine = true
            )
            Text(
                text = stringResource(R.string.todo_category_manager_color),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    NoColorChip(
                        selected = categoryColorInput.isBlank(),
                        onClick = { onCategoryColorInputChange("") }
                    )
                }
                items(items = PRESET_COLORS, key = { it }) { hex ->
                    ColorSwatch(
                        hex = hex,
                        selected = categoryColorInput.equals(hex, ignoreCase = true),
                        onClick = { onCategoryColorInputChange(hex) }
                    )
                }
            }
            OutlinedTextField(
                value = categoryIconInput,
                onValueChange = onCategoryIconInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { state ->
                        focusedInputCount = if (state.hasFocus || state.isCaptured) 1 else 0
                    },
                label = { Text(stringResource(R.string.todo_category_manager_icon_optional)) },
                singleLine = true
            )

            Button(
                onClick = onSaveCategoryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (editingCategoryId == null) {
                        stringResource(R.string.todo_category_manager_add)
                    } else {
                        stringResource(R.string.todo_category_manager_update)
                    }
                )
            }

            Text(
                text = stringResource(R.string.todo_category_manager_current),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(items = categories, key = { it.id ?: it.name }) { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        category.id?.let { categoryId ->
                            IconButton(onClick = { onCategoryEditClick(categoryId) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(
                                        R.string.todo_category_manager_edit
                                    )
                                )
                            }
                            IconButton(onClick = { onCategoryDeleteClick(categoryId) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(
                                        R.string.todo_category_manager_delete
                                    )
                                )
                            }
                        }
                    }
                }
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.todo_category_manager_close))
            }
            Spacer(Modifier.size(10.dp))
        }
    }
}

@Composable
private fun NoColorChip(
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFFDCE7FF) else Color(0xFFEEF1F7),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = stringResource(R.string.todo_category_manager_no_color),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF4A6697)
        )
    }
}

@Composable
private fun ColorSwatch(
    hex: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = runCatching { Color(parseColor(hex)) }.getOrElse { Color(0xFFB0B7C5) }
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(color = color, shape = CircleShape)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) Color(0xFF2E3D59) else Color(0xFFCBD2E1),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

private val PRESET_COLORS = listOf(
    "#4A6697",
    "#2E7D32",
    "#C62828",
    "#6A1B9A",
    "#EF6C00",
    "#00838F",
    "#5D4037",
    "#455A64"
)

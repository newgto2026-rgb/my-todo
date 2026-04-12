package com.example.myfirstapp.feature.todo.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.ui.TodoItemRow

@Composable
fun TodoListRoute(
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is TodoListSideEffect.ShowSnackbar -> snackbarHostState.showSnackbar(sideEffect.message)
            }
        }
    }

    TodoListScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun TodoListScreen(
    uiState: TodoListUiState,
    onAction: (TodoListAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val (title, subtitle) = headerTextFor(uiState.selectedFilter)
    val completionProgress = completionProgress(uiState)

    Scaffold(
        containerColor = Color(0xFFF5F6FB),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(TodoListAction.OnAddClick) },
                containerColor = Color(0xFF4A6697),
                contentColor = Color.White,
                modifier = Modifier
                    .size(58.dp)
                    .testTag("add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            BottomFilterBar(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = { onAction(TodoListAction.OnFilterChange(it)) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(14.dp))
            AppHeader()
            Spacer(Modifier.height(22.dp))

            HeaderSummary(
                title = title,
                subtitle = subtitle,
                selectedFilter = uiState.selectedFilter,
                completionProgress = completionProgress
            )
            CategoryFilterBar(
                categories = uiState.categories,
                selectedCategoryId = uiState.selectedCategoryId,
                onCategorySelected = { onAction(TodoListAction.OnCategoryFilterChange(it)) }
            )
            Spacer(Modifier.height(12.dp))

            if (uiState.items.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = uiState.items, key = { item -> item.id }) { item ->
                        val rowSubtitle = when {
                            item.isDone -> "Completed"
                            item.isReminderEnabled && !item.reminderDateTimeText.isNullOrBlank() -> "Reminder ${item.reminderDateTimeText}"
                            isToday(item.dueDateText) -> "Today"
                            !item.dueDateText.isNullOrBlank() -> formatDueDateLabel(item.dueDateText)
                            else -> null
                        }
                        TodoItemRow(
                            title = item.title,
                            dueDateText = rowSubtitle,
                            isDone = item.isDone,
                            isEmphasized = !item.isDone && rowSubtitle == "Today",
                            onToggleDone = { onAction(TodoListAction.OnToggleDone(item.id)) },
                            onClick = { onAction(TodoListAction.OnEditClick(item.id)) },
                            categoryName = item.categoryName,
                            categoryColorHex = item.categoryColorHex
                        )
                    }
                }
            } else {
                EmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    message = emptyMessage(uiState.selectedFilter)
                )
            }
        }
    }

    if (uiState.isEditDialogVisible) {
        EditTodoBottomSheet(
            title = uiState.draftTitle,
            dueDateInput = uiState.draftDueDateInput,
            reminderEnabled = uiState.draftReminderEnabled,
            reminderDateTimeInput = uiState.draftReminderDateTimeInput,
            reminderRepeatType = uiState.draftReminderRepeatType,
            categories = uiState.categories,
            selectedCategoryId = uiState.draftCategoryId,
            errorMessage = uiState.errorMessage,
            onTitleChange = { onAction(TodoListAction.OnTitleChange(it)) },
            onDateInputChange = { onAction(TodoListAction.OnDueDateInputChange(it)) },
            onReminderEnabledChange = { onAction(TodoListAction.OnReminderEnabledChange(it)) },
            onReminderDateTimeInputChange = { onAction(TodoListAction.OnReminderDateTimeInputChange(it)) },
            onReminderRepeatTypeChange = { onAction(TodoListAction.OnReminderRepeatTypeChange(it)) },
            onCategorySelected = { onAction(TodoListAction.OnCategorySelectedInEditor(it)) },
            onManageCategoriesClick = { onAction(TodoListAction.OnManageCategoriesClick) },
            onDismiss = { onAction(TodoListAction.OnDismissDialog) },
            onSave = { onAction(TodoListAction.OnSaveClick) },
            onDelete = {
                uiState.editingItem?.id?.let { onAction(TodoListAction.OnDeleteClick(it)) }
                onAction(TodoListAction.OnDismissDialog)
            },
            showDelete = uiState.editingItem != null
        )
    }

    if (uiState.isCategoryManagerVisible) {
        CategoryManagerBottomSheet(
            categories = uiState.categories,
            categoryNameInput = uiState.categoryNameInput,
            categoryColorInput = uiState.categoryColorInput,
            categoryIconInput = uiState.categoryIconInput,
            editingCategoryId = uiState.editingCategoryId,
            onCategoryNameInputChange = { onAction(TodoListAction.OnCategoryNameInputChange(it)) },
            onCategoryColorInputChange = { onAction(TodoListAction.OnCategoryColorInputChange(it)) },
            onCategoryIconInputChange = { onAction(TodoListAction.OnCategoryIconInputChange(it)) },
            onCategoryEditClick = { onAction(TodoListAction.OnCategoryEditClick(it)) },
            onCategoryDeleteClick = { onAction(TodoListAction.OnCategoryDeleteClick(it)) },
            onSaveCategoryClick = { onAction(TodoListAction.OnCategorySaveClick) },
            onDismiss = { onAction(TodoListAction.OnDismissCategoryManager) }
        )
    }
}

private fun headerTextFor(filter: TodoFilter): Pair<String, String> = when (filter) {
    TodoFilter.ALL -> "All Tasks" to "Sorted by due date"
    TodoFilter.TODAY -> "Today" to java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM d", java.util.Locale.ENGLISH))
    TodoFilter.COMPLETED -> "Completed" to "Finished tasks"
}

private fun completionProgress(uiState: TodoListUiState): Float {
    if (uiState.items.isEmpty()) return 0f
    return uiState.items.count { it.isDone }.toFloat() / uiState.items.size.toFloat()
}

private fun formatDueDateLabel(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return runCatching {
        val parsed = java.time.LocalDate.parse(raw, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        parsed.format(java.time.format.DateTimeFormatter.ofPattern("MMM d", java.util.Locale.ENGLISH))
    }.getOrDefault(raw)
}

private fun isToday(raw: String?): Boolean {
    if (raw.isNullOrBlank()) return false
    return runCatching {
        java.time.LocalDate.parse(raw, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE) == java.time.LocalDate.now()
    }.getOrDefault(false)
}

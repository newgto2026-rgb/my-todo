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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import com.example.myfirstapp.core.model.TodoFilter
import com.example.myfirstapp.core.ui.TodoItemRow
import com.example.myfirstapp.feature.todo.impl.R

@Composable
fun TodoListRoute(
    presetFilter: TodoFilter,
    initialEditTodoId: Long? = null,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val navBackStackEntry = LocalViewModelStoreOwner.current as? NavBackStackEntry
    val isModalVisible = uiState.isEditDialogVisible || uiState.isCategoryManagerVisible

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is TodoListSideEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(context.getString(sideEffect.messageRes))
                }
            }
        }
    }

    LaunchedEffect(presetFilter, uiState.selectedFilter) {
        if (uiState.selectedFilter != presetFilter) {
            viewModel.onAction(TodoListAction.OnFilterChange(presetFilter))
        }
    }

    LaunchedEffect(initialEditTodoId) {
        if (initialEditTodoId != null) {
            viewModel.onAction(TodoListAction.OnEditClick(initialEditTodoId))
        }
    }

    LaunchedEffect(navBackStackEntry, isModalVisible) {
        navBackStackEntry?.savedStateHandle?.set("app_back_blocked", isModalVisible)
    }

    DisposableEffect(navBackStackEntry) {
        onDispose {
            navBackStackEntry?.savedStateHandle?.set("app_back_blocked", false)
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
    val (title, subtitle) = headerTextFor(
        filter = uiState.selectedFilter,
        allTitle = stringResource(R.string.todo_header_all_title),
        allSubtitle = stringResource(R.string.todo_header_all_subtitle),
        todayTitle = stringResource(R.string.todo_filter_today),
        completedTitle = stringResource(R.string.todo_header_completed_title),
        completedSubtitle = stringResource(R.string.todo_header_completed_subtitle)
    )
    val completionProgress = completionProgress(uiState)
    val rowCompletedText = stringResource(R.string.todo_row_subtitle_completed)
    val rowTodayText = stringResource(R.string.todo_row_subtitle_today)
    val uncategorizedText = stringResource(R.string.todo_category_uncategorized)

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
                        val dueDateLabel = when {
                            isToday(item.dueDateText) -> rowTodayText
                            !item.dueDateText.isNullOrBlank() -> formatDueDateLabel(item.dueDateText)
                            else -> null
                        }
                        val dueLabel = buildDueLabel(dueDateLabel, item.dueTimeText)
                        val leadLabel = when (item.reminderLeadMinutes) {
                            0 -> stringResource(R.string.todo_reminder_lead_at_time)
                            5 -> stringResource(R.string.todo_reminder_lead_5m)
                            10 -> stringResource(R.string.todo_reminder_lead_10m)
                            30 -> stringResource(R.string.todo_reminder_lead_30m)
                            60 -> stringResource(R.string.todo_reminder_lead_60m)
                            else -> null
                        }
                        val reminderLabel = if (item.isReminderEnabled && !leadLabel.isNullOrBlank() && !item.isDone) {
                            leadLabel
                        } else {
                            null
                        }
                        val rowDueLabel = when {
                            item.isDone -> rowCompletedText
                            !dueLabel.isNullOrBlank() -> dueLabel
                            else -> null
                        }
                        TodoItemRow(
                            title = item.title,
                            dueDateText = rowDueLabel,
                            reminderText = reminderLabel,
                            isDone = item.isDone,
                            isEmphasized = !item.isDone && dueDateLabel == rowTodayText,
                            isReminderEnabled = item.isReminderEnabled,
                            onToggleDone = { onAction(TodoListAction.OnToggleDone(item.id)) },
                            onClick = { onAction(TodoListAction.OnEditClick(item.id)) },
                            categoryName = item.categoryName ?: uncategorizedText,
                            categoryColorHex = item.categoryColorHex
                        )
                    }
                }
            } else {
                EmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    message = stringResource(emptyMessage(uiState.selectedFilter))
                )
            }
        }
    }

    if (uiState.isEditDialogVisible) {
        EditTodoBottomSheet(
            title = uiState.draftTitle,
            dueDateInput = uiState.draftDueDateInput,
            dueTimeInput = uiState.draftDueTimeInput,
            reminderEnabled = uiState.draftReminderEnabled,
            reminderLeadMinutes = uiState.draftReminderLeadMinutes ?: DEFAULT_REMINDER_LEAD_MINUTES,
            categories = uiState.categories,
            selectedCategoryId = uiState.draftCategoryId,
            errorMessageRes = uiState.errorMessageRes,
            onTitleChange = { onAction(TodoListAction.OnTitleChange(it)) },
            onDateInputChange = { onAction(TodoListAction.OnDueDateInputChange(it)) },
            onDueTimeInputChange = { onAction(TodoListAction.OnDueTimeInputChange(it)) },
            onReminderEnabledChange = { onAction(TodoListAction.OnReminderEnabledChange(it)) },
            onReminderLeadMinutesChange = { onAction(TodoListAction.OnReminderLeadMinutesChange(it)) },
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

private fun headerTextFor(
    filter: TodoFilter,
    allTitle: String,
    allSubtitle: String,
    todayTitle: String,
    completedTitle: String,
    completedSubtitle: String
): Pair<String, String> = when (filter) {
    TodoFilter.ALL -> allTitle to allSubtitle
    TodoFilter.TODAY -> {
        val subtitle = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("MMMM d", java.util.Locale.getDefault())
        )
        todayTitle to subtitle
    }
    TodoFilter.COMPLETED -> completedTitle to completedSubtitle
}

private fun completionProgress(uiState: TodoListUiState): Float {
    if (uiState.items.isEmpty()) return 0f
    return uiState.items.count { it.isDone }.toFloat() / uiState.items.size.toFloat()
}

private fun formatDueDateLabel(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return runCatching {
        val parsed = java.time.LocalDate.parse(raw, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        parsed.format(java.time.format.DateTimeFormatter.ofPattern("MMM d", java.util.Locale.getDefault()))
    }.getOrDefault(raw)
}

private fun buildDueLabel(dueDate: String?, dueTime: String?): String? {
    if (dueDate.isNullOrBlank()) return null
    return if (dueTime.isNullOrBlank()) dueDate else "$dueDate $dueTime"
}

private fun isToday(raw: String?): Boolean {
    if (raw.isNullOrBlank()) return false
    return runCatching {
        java.time.LocalDate.parse(raw, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE) == java.time.LocalDate.now()
    }.getOrDefault(false)
}

package com.example.myfirstapp.core.datastore.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.myfirstapp.core.model.TodoFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesDataSource {

    override val selectedTodoFilter: Flow<TodoFilter> =
        dataStore.data.map { prefs ->
            val stored = prefs[SELECTED_TODO_FILTER]
            stored?.let { value -> TodoFilter.entries.find { it.name == value } } ?: TodoFilter.ALL
        }

    override val selectedTodoCategoryFilter: Flow<Long?> =
        dataStore.data.map { prefs -> prefs[SELECTED_TODO_CATEGORY_FILTER] }

    override suspend fun setSelectedTodoFilter(filter: TodoFilter) {
        dataStore.edit { prefs ->
            prefs[SELECTED_TODO_FILTER] = filter.name
        }
    }

    override suspend fun setSelectedTodoCategoryFilter(categoryId: Long?) {
        dataStore.edit { prefs ->
            if (categoryId == null) {
                prefs.remove(SELECTED_TODO_CATEGORY_FILTER)
            } else {
                prefs[SELECTED_TODO_CATEGORY_FILTER] = categoryId
            }
        }
    }

    private companion object {
        val SELECTED_TODO_FILTER = stringPreferencesKey("selected_todo_filter")
        val SELECTED_TODO_CATEGORY_FILTER = longPreferencesKey("selected_todo_category_filter")
    }
}

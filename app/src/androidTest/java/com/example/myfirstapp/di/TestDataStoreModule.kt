package com.example.myfirstapp.di

import com.example.myfirstapp.core.datastore.di.DataStoreModule
import com.example.myfirstapp.core.datastore.source.UserPreferencesDataSource
import com.example.myfirstapp.core.model.TodoFilter
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class]
)
object TestDataStoreModule {

    @Provides
    @Singleton
    fun provideUserPreferencesDataSource(): UserPreferencesDataSource = InMemoryUserPreferencesDataSource()
}

private class InMemoryUserPreferencesDataSource : UserPreferencesDataSource {
    private val selectedFilter = MutableStateFlow(TodoFilter.ALL)

    override val selectedTodoFilter: Flow<TodoFilter> = selectedFilter

    override suspend fun setSelectedTodoFilter(filter: TodoFilter) {
        selectedFilter.value = filter
    }
}

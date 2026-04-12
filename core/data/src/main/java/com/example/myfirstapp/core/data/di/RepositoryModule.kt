package com.example.myfirstapp.core.data.di

import com.example.myfirstapp.core.data.repository.ReminderRepositoryImpl
import com.example.myfirstapp.core.data.repository.TodoRepositoryImpl
import com.example.myfirstapp.core.domain.repository.ReminderRepository
import com.example.myfirstapp.core.domain.repository.TodoCategoryRepository
import com.example.myfirstapp.core.domain.repository.TodoFilterRepository
import com.example.myfirstapp.core.domain.repository.TodoItemRepository
import com.example.myfirstapp.core.domain.repository.TodoReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTodoItemRepository(impl: TodoRepositoryImpl): TodoItemRepository

    @Binds
    @Singleton
    abstract fun bindTodoCategoryRepository(impl: TodoRepositoryImpl): TodoCategoryRepository

    @Binds
    @Singleton
    abstract fun bindTodoFilterRepository(impl: TodoRepositoryImpl): TodoFilterRepository

    @Binds
    @Singleton
    abstract fun bindTodoReminderRepository(impl: TodoRepositoryImpl): TodoReminderRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository
}

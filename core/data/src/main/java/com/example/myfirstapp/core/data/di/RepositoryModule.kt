package com.example.myfirstapp.core.data.di

import com.example.myfirstapp.core.data.repository.ReminderRepositoryImpl
import com.example.myfirstapp.core.data.repository.TodoRepositoryImpl
import com.example.myfirstapp.core.domain.repository.ReminderRepository
import com.example.myfirstapp.core.domain.repository.TodoRepository
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
    abstract fun bindTodoRepository(impl: TodoRepositoryImpl): TodoRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(impl: ReminderRepositoryImpl): ReminderRepository
}

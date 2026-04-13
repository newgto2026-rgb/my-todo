package com.example.myfirstapp.feature.calendar.entry.di

import com.example.myfirstapp.core.ui.navigation.AppFeatureEntry
import com.example.myfirstapp.feature.calendar.impl.navigation.CalendarFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarFeatureEntryModule {

    @Binds
    @IntoSet
    abstract fun bindCalendarFeatureEntry(entry: CalendarFeatureEntryImpl): AppFeatureEntry
}

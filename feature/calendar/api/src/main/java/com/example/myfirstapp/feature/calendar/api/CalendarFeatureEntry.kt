package com.example.myfirstapp.feature.calendar.api

import com.example.myfirstapp.core.ui.navigation.AppFeatureEntry
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
data object CalendarRoute : NavKey

interface CalendarFeatureEntry : AppFeatureEntry

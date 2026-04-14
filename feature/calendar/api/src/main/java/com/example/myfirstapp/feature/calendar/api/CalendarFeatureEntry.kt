package com.example.myfirstapp.feature.calendar.api

import com.example.myfirstapp.core.navigation.AppFeatureEntry
import kotlinx.serialization.Serializable

@Serializable
data object CalendarRoute

interface CalendarFeatureEntry : AppFeatureEntry

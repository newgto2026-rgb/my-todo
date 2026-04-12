package com.example.myfirstapp.core.domain.scheduler

import com.example.myfirstapp.core.model.Reminder

interface ReminderScheduler {
    suspend fun schedule(reminder: Reminder)
    suspend fun cancel(reminderId: Long)
    suspend fun rescheduleAll()
}

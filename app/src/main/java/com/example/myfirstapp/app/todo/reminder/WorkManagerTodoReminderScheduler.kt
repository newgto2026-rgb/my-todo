package com.example.myfirstapp.app.todo.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myfirstapp.core.domain.scheduler.TodoReminderScheduler
import com.example.myfirstapp.core.domain.usecase.GetActiveTodoRemindersUseCase
import com.example.myfirstapp.core.model.TodoItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerTodoReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getActiveTodoRemindersUseCase: GetActiveTodoRemindersUseCase
) : TodoReminderScheduler {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    override suspend fun schedule(todo: TodoItem) {
        val triggerAt = todo.reminderAtEpochMillis
        if (!todo.isReminderEnabled || triggerAt == null) {
            cancel(todo.id)
            return
        }

        val delayMillis = (triggerAt - System.currentTimeMillis()).coerceAtLeast(0L)
        val request = OneTimeWorkRequestBuilder<TodoReminderWorker>()
            .setInputData(TodoReminderWorker.inputData(todo.id))
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            TodoReminderConstants.workName(todo.id),
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override suspend fun cancel(todoId: Long) {
        workManager.cancelUniqueWork(TodoReminderConstants.workName(todoId))
    }

    override suspend fun rescheduleAll() {
        val todos = getActiveTodoRemindersUseCase()
        todos.forEach { schedule(it) }
    }
}

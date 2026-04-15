package com.example.myfirstapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfirstapp.core.database.dao.CategoryDao
import com.example.myfirstapp.core.database.dao.ReminderDao
import com.example.myfirstapp.core.database.dao.TodoDao
import com.example.myfirstapp.core.database.entity.CategoryEntity
import com.example.myfirstapp.core.database.entity.ReminderEntity
import com.example.myfirstapp.core.database.entity.TodoEntity

@Database(
    entities = [TodoEntity::class, CategoryEntity::class, ReminderEntity::class],
    version = 7,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
}

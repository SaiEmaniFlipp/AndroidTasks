package com.saiemani.tasks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saiemani.tasks.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TasksDatabase: RoomDatabase() {

    abstract fun  taskDao(): TasksDao
}

package com.saiemani.tasks

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TasksDatabase: RoomDatabase() {

    abstract fun  taskDao(): TasksDao
}

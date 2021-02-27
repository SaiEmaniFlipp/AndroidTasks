package com.saiemani.tasks.data

import androidx.lifecycle.LiveData

interface ITasksDataSource {

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()
}

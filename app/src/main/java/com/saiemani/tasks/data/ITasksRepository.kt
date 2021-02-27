package com.saiemani.tasks.data

import androidx.lifecycle.LiveData
import com.saiemani.tasks.data.Result
import com.saiemani.tasks.data.Task

interface ITasksRepository {

    fun observeTasks(): LiveData<Result<List<Task>>>

    suspend fun getTasks(): Result<List<Task>>

    suspend fun getTask(taskId: String): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

}

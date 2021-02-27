package com.saiemani.tasks

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.Result
import com.saiemani.tasks.data.Task
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository @Inject constructor() : ITasksRepository {

    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private var shouldReturnError = false

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    private suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { observableTasks.value = getTasks() }
        return observableTasks
    }

    override suspend fun getTasks(): Result<List<Task>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        tasksServiceData[taskId]?.let {
            return Result.Success(it)
        }
        return Result.Error(Exception("Could not find task"))
    }

    override suspend fun saveTask(task: Task) {
        tasksServiceData[task.id] = task
    }

    override suspend fun completeTask(taskId: String) {
        tasksServiceData[taskId]?.let { task ->
            val newTask = Task(task.title, isCompleted = true, task.id)
            tasksServiceData[taskId] = newTask
        }
        refreshTasks()
    }

    override suspend fun activateTask(taskId: String) {
        tasksServiceData[taskId]?.let { task ->
            val newTask = Task(task.title, isCompleted = false, task.id)
            tasksServiceData[taskId] = newTask
        }
        refreshTasks()
    }

    override suspend fun clearCompletedTasks() {
        tasksServiceData = tasksServiceData.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    @VisibleForTesting
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
}

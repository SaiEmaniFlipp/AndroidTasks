package com.saiemani.tasks.data

import androidx.lifecycle.LiveData
import com.saiemani.tasks.data.ITasksDataSource
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.Result
import com.saiemani.tasks.data.Task
import com.saiemani.tasks.util.wrapEspressoIdlingResource
import kotlinx.coroutines.*

class TasksRepository(
    private val tasksLocalDataSource: ITasksDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ITasksRepository {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksLocalDataSource.observeTasks()
    }

    override suspend fun getTasks(): Result<List<Task>> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.getTasks()
        }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        wrapEspressoIdlingResource {
            return tasksLocalDataSource.getTask(taskId)
        }
    }

    override suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { tasksLocalDataSource.saveTask(task) }
        }
    }

    override suspend fun completeTask(taskId: String) {
        coroutineScope {
            launch { tasksLocalDataSource.completeTask(taskId) }
        }
    }

    override suspend fun activateTask(taskId: String) {
        coroutineScope {
            launch { tasksLocalDataSource.activateTask(taskId) }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { tasksLocalDataSource.clearCompletedTasks() }
        }
    }
}

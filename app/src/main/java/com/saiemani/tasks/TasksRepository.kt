package com.saiemani.tasks

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

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
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }
}

package com.saiemani.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class TasksLocalDataSource internal constructor(
    private val tasksDao: TasksDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ITasksDataSource {

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return tasksDao.observeTasks().map { Result.Success(it) }
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        /*return@withContext try {
            Result.Success(tasksDao.getTasks())
        } catch (e: Exception) {
            Result.Error(e)
        }*/
        return@withContext Result.Success(Collections.singletonList(Task("First Task")))
    }


    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = tasksDao.getTaskById(taskId)
            if (task != null) {
                return@withContext Result.Success(task)
            } else {
                return@withContext Result.Error(Exception("Task not found!"))
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e)
        }
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        tasksDao.insertTask(task)
    }

    override suspend fun completeTask(taskId: String) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(taskId: String) = withContext(ioDispatcher) {
        tasksDao.updateCompleted(taskId, false)
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        tasksDao.deleteCompletedTasks()
    }
}

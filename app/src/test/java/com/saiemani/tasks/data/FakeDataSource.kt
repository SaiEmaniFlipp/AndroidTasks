package com.saiemani.tasks.data

import androidx.lifecycle.LiveData

class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()) : ITasksDataSource {
    override suspend fun getTasks(): Result<List<Task>> {
        tasks?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            Exception("Tasks not found")
        )
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        tasks?.firstOrNull { it.id == taskId }?.let {
            return Result.Success(
                it
            )
        }
        return Result.Error(
            Exception("Task not found")
        )
    }

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
    }

    override suspend fun completeTask(taskId: String) {
        tasks?.firstOrNull { it.id == taskId }?.let { it.isCompleted = true }
    }

    override suspend fun activateTask(taskId: String) {
        tasks?.firstOrNull { it.id == taskId }?.let { it.isCompleted = false }
    }

    override suspend fun clearCompletedTasks() {
        tasks?.removeIf { it.isCompleted }
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("not implemented")
    }
}

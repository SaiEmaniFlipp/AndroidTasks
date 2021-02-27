package com.saiemani.tasks.util

import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.Task
import kotlinx.coroutines.runBlocking

/**
 * A blocking version of ITasksRepository.saveTask to minimize the number of times we have to
 * explicitly add <code>runBlocking { ... }</code> in our tests
 */
fun ITasksRepository.saveTaskBlocking(task: Task) = runBlocking {
    this@saveTaskBlocking.saveTask(task)
}

fun ITasksRepository.getTasksBlocking() = runBlocking {
    this@getTasksBlocking.getTasks()
}

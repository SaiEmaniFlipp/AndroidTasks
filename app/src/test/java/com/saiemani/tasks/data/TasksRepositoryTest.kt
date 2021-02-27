package com.saiemani.tasks.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration Test for [TasksRepository]
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksRepositoryTest {

    private val task1 = Task("Title1")
    private val task2 = Task("Title2")
    private val task3 = Task("Title3")
    private val newTask = Task("Title new")
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var tasksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tasksRepository: TasksRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = TasksRepository(
            tasksLocalDataSource, Dispatchers.Main
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTasks_emptyRepositoryAndUninitializedCache() = mainCoroutineRule.runBlockingTest {
        val emptySource = FakeDataSource()
        val tasksRepository = TasksRepository(
            emptySource, Dispatchers.Main
        )

        assertThat(tasksRepository.getTasks() is Result.Success).isTrue()
    }

    @Test
    fun getTasks_requestsAllTasksFromLocalDataSource() = mainCoroutineRule.runBlockingTest {
        // When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks() as Result.Success

        // Then tasks are loaded from the remote data source
        assertThat(tasks.data).isEqualTo(localTasks)
    }

    @Test
    fun saveTask_savesToLocalDataSource() = mainCoroutineRule.runBlockingTest {
        // Make sure newTask is not in the local datasources
        assertThat(tasksLocalDataSource.tasks).doesNotContain(newTask)

        // When a task is saved to the tasks repository
        tasksRepository.saveTask(newTask)

        // Then the local sources are called
        assertThat(tasksLocalDataSource.tasks).contains(newTask)
    }

    @Test
    fun getTasks_WithDataSourcesUnavailable_returnsError() = mainCoroutineRule.runBlockingTest {
        // When both sources are unavailable
        tasksLocalDataSource.tasks = null

        // The repository returns an error
        assertThat(tasksRepository.getTasks()).isInstanceOf(Result.Error::class.java)
    }

    @Test
    fun completeTask_completesTaskInCache() = mainCoroutineRule.runBlockingTest {
        // Save a task
        tasksRepository.saveTask(newTask)

        // Make sure it's active
        assertThat((tasksRepository.getTask(newTask.id) as Result.Success).data.isCompleted).isFalse()

        // Mark is as complete
        tasksRepository.completeTask(newTask.id)

        // Verify it's now completed
        assertThat((tasksRepository.getTask(newTask.id) as Result.Success).data.isCompleted).isTrue()
    }

    @Test
    fun activateTask_activeTaskToLocalCache() = mainCoroutineRule.runBlockingTest {
        // Save a task
        tasksRepository.saveTask(newTask)
        tasksRepository.completeTask(newTask.id)

        // Make sure it's completed
        assertThat((tasksRepository.getTask(newTask.id) as Result.Success).data.isActive).isFalse()

        // Mark is as active
        tasksRepository.activateTask(newTask.id)

        // Verify it's now activated
        val result = tasksRepository.getTask(newTask.id) as Result.Success
        assertThat(result.data.isActive).isTrue()
    }

    @Test
    fun clearCompletedTasks() = mainCoroutineRule.runBlockingTest {
        val completedTask = task1.copy().apply { isCompleted = true }

        tasksRepository.clearCompletedTasks()

        val tasks = (tasksRepository.getTasks() as? Result.Success)?.data

        assertThat(tasks).hasSize(1)
        assertThat(tasks).contains(task3)
        assertThat(tasks).doesNotContain(completedTask)
    }
}

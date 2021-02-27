package com.saiemani.tasks.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.saiemani.tasks.*
import com.saiemani.tasks.data.FakeRepository
import com.saiemani.tasks.data.MainCoroutineRule
import com.saiemani.tasks.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TasksViewModel]
 */
@ExperimentalCoroutinesApi
class TasksViewModelTest {

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tasks to 3, with one active and two completed
        tasksRepository = FakeRepository()
        val task1 = Task("Title1")
        val task2 = Task("Title2", true)
        val task3 = Task("Title3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @Test
    fun loadAllTasksFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Given an initialized TasksViewModel with initialized tasks
        // Trigger loading of tasks
        tasksViewModel.loadTasks(true)

        // Observe the items to keep LiveData emitting
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is shown
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isTrue()

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            assertThat(tasksViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun loadTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized TasksViewModel with initialized tasks

        // Load tasks
        tasksViewModel.loadTasks(true)
        // Observe the items to keep LiveData emitting
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is hidden
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And data correctly loaded
            assertThat(tasksViewModel.items.getOrAwaitValue()).hasSize(3)
        }
    }

    @Test
    fun loadTasks_error() {
        // Make the repository return errors
        tasksRepository.setReturnError(true)

        // Load tasks
        tasksViewModel.loadTasks(true)
        // Observe the items to keep LiveData emitting
        tasksViewModel.items.observeForTesting {

            // Then progress indicator is hidden
            assertThat(tasksViewModel.dataLoading.getOrAwaitValue()).isFalse()

            // And the list of items is empty
            assertThat(tasksViewModel.items.getOrAwaitValue()).isEmpty()

            // And the snackbar updated
            assertSnackbarMessage(tasksViewModel.snackbarText, R.string.str_loading_tasks_error)
        }
    }

    @Test
    fun clickOnFab_showsAddTaskUi() {
        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the event is triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled()).isNotNull()
    }

    @Test
    fun clearCompletedTasks_clearsTasks() = mainCoroutineRule.runBlockingTest {
        // When completed tasks are cleared
        tasksViewModel.clearCompletedTasks()

        // Fetch tasks
        tasksViewModel.loadTasks(true)

        // Fetch tasks
        val allTasks = tasksViewModel.items.getOrAwaitValue()

        // Verify active task is not cleared
        assertThat(allTasks).hasSize(1)

        // Verify snackbar is updated
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.str_completed_tasks_cleared
        )
    }

    @Test
    fun activateTask_dataAndSnackbarUpdated() {
        // With a repository that has a completed task
        val task = Task("Title", true)
        tasksRepository.addTasks(task)

        // Activate task
        tasksViewModel.completeTask(task, false)

        // Verify the task is active
        assertThat(tasksRepository.tasksServiceData[task.id]?.isActive).isTrue()

        // The snackbar is updated
        assertSnackbarMessage(
            tasksViewModel.snackbarText, R.string.str_task_marked_active
        )
    }
}

package com.saiemani.tasks.add_task

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.saiemani.tasks.FakeRepository
import com.saiemani.tasks.R
import com.saiemani.tasks.assertSnackbarMessage
import com.saiemani.tasks.data.MainCoroutineRule
import com.saiemani.tasks.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [AddTaskViewModel]
 */
@ExperimentalCoroutinesApi
class AddTaskViewModelTest {
    // Subject under test
    private lateinit var addTaskViewModel: AddTaskViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val task = Task("Title1")

    @Before
    fun setupViewModel() {
        // We initialise the repository with no tasks
        tasksRepository = FakeRepository()

        // Create class under test
        addTaskViewModel = AddTaskViewModel(tasksRepository)
    }

    @Test
    fun saveNewTaskToRepository_showsSuccessMessageUi() {
        val newTitle = "New Task Title"
        val newDescription = "Some Task Description"
        (addTaskViewModel).apply {
            taskDescription.value = newTitle
        }
        addTaskViewModel.saveTask()

        val newTask = tasksRepository.tasksServiceData.values.first()

        // Then a task is saved in the repository and the view updated
        assertThat(newTask.title).isEqualTo(newTitle)
    }

    @Test
    fun saveNewTaskToRepository_emptyTitle_error() {
        saveTaskAndAssertSnackbarError("")
    }

    @Test
    fun saveNewTaskToRepository_nullTitle_error() {
        saveTaskAndAssertSnackbarError(null)
    }

    private fun saveTaskAndAssertSnackbarError(title: String?) {
        (addTaskViewModel).apply {
            this.taskDescription.value = title
        }

        // When saving an incomplete task
        addTaskViewModel.saveTask()

        // Then the snackbar shows an error
        assertSnackbarMessage(addTaskViewModel.snackbarText, R.string.str_empty_task_message)
    }
}

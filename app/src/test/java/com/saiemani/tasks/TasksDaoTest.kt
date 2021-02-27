package com.saiemani.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.saiemani.tasks.data.Task
import com.saiemani.tasks.data.local.TasksDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TasksDaoTest {

    private lateinit var database: TasksDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TasksDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTaskAndGetById() = runBlockingTest {
        // GIVEN - insert a task
        val task = Task("title")
        database.taskDao().insertTask(task)

        // WHEN - Get the task by id from the database
        val loaded = database.taskDao().getTaskById(task.id)

        // THEN - The loaded data contains the expected values
        MatcherAssert.assertThat(loaded as Task, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(task.id))
        MatcherAssert.assertThat(loaded.title, `is`(task.title))
        MatcherAssert.assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun insertTaskReplacesOnConflict() = runBlockingTest {
        // Given that a task is inserted
        val task = Task("title")
        database.taskDao().insertTask(task)

        // When a task with the same id is inserted
        val newTask = Task("title2", true, task.id)
        database.taskDao().insertTask(newTask)

        // THEN - The loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(task.id)
        MatcherAssert.assertThat(loaded?.id, `is`(task.id))
        MatcherAssert.assertThat(loaded?.title, CoreMatchers.`is`("title2"))
        MatcherAssert.assertThat(loaded?.isCompleted, CoreMatchers.`is`(true))
    }

    @Test
    fun insertTaskAndGetTasks() = runBlockingTest {
        // GIVEN - insert a task
        val task = Task("title")
        database.taskDao().insertTask(task)

        // WHEN - Get tasks from the database
        val tasks = database.taskDao().getTasks()

        // THEN - There is only 1 task in the database, and contains the expected values
        MatcherAssert.assertThat(tasks.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(tasks[0].id, `is`(task.id))
        MatcherAssert.assertThat(tasks[0].title, `is`(task.title))
        MatcherAssert.assertThat(tasks[0].isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateCompletedAndGetById() = runBlockingTest {
        // When inserting a task
        val task = Task("title", true)
        database.taskDao().insertTask(task)

        // When the task is updated
        database.taskDao().updateCompleted(task.id, false)

        // THEN - The loaded data contains the expected values
        val loaded = database.taskDao().getTaskById(task.id)
        MatcherAssert.assertThat(loaded?.id, `is`(task.id))
        MatcherAssert.assertThat(loaded?.title, `is`(task.title))
        MatcherAssert.assertThat(loaded?.isCompleted, CoreMatchers.`is`(false))
    }

    @Test
    fun deleteTaskByIdAndGettingTasks() = runBlockingTest {
        // Given a task inserted
        val task = Task("title")
        database.taskDao().insertTask(task)

        // When deleting a task by id
        database.taskDao().deleteTaskById(task.id)

        // THEN - The list is empty
        val tasks = database.taskDao().getTasks()
        MatcherAssert.assertThat(tasks.isEmpty(), CoreMatchers.`is`(true))
    }

    @Test
    fun deleteCompletedTasksAndGettingTasks() = runBlockingTest {
        // Given a completed task inserted
        database.taskDao().insertTask(Task("completed", true))

        // When deleting completed tasks
        database.taskDao().deleteCompletedTasks()

        // THEN - The list is empty
        val tasks = database.taskDao().getTasks()
        MatcherAssert.assertThat(tasks.isEmpty(), CoreMatchers.`is`(true))
    }
}

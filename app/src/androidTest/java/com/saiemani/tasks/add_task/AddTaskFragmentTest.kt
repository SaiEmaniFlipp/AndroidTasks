package com.saiemani.tasks.add_task

import android.content.Context
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.saiemani.tasks.R
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.Result
import com.saiemani.tasks.data.TasksRepository
import com.saiemani.tasks.di.TasksRepositoryModule
import com.saiemani.tasks.launchFragmentInHiltContainer
import com.saiemani.tasks.tasks.ADD_RESULT_OK
import com.saiemani.tasks.util.getTasksBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

/**
 * Integration test for the Add Task screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@UninstallModules(TasksRepositoryModule::class)
@HiltAndroidTest
class AddTaskFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ITasksRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    @Test
    fun emptyTask_isNotSaved() {
        // GIVEN - On the "Add Task" screen
        launchFragmentInHiltContainer<AddTasksFragment>(Bundle(), R.style.AppTheme)

        // WHEN - Enter invalid title and description combination and click save
        Espresso.onView(withId(R.id.task_edit_text)).perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.save_task_fab)).perform(ViewActions.click())

        // THEN - Entered Task is still displayed (a correct task would close it).
        Espresso.onView(withId(R.id.task_edit_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun validTask_navigatesBack() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<AddTasksFragment>(Bundle(), R.style.AppTheme) {
            Navigation.setViewNavController(view!!, navController)
        }

        // WHEN - Valid title and description combination and click save
        Espresso.onView(withId(R.id.task_edit_text))
            .perform(ViewActions.replaceText("title"))
        Espresso.onView(withId(R.id.save_task_fab)).perform(ViewActions.click())

        // THEN - Verify that we navigated back to the tasks screen.
        Mockito.verify(navController).navigate(
            AddTasksFragmentDirections
                .actionAddTasksFragmentToTasksFragment(ADD_RESULT_OK)
        )
    }

    @Test
    fun validTask_isSaved() {
        // GIVEN - On the "Add Task" screen.
        val navController = Mockito.mock(NavController::class.java)
        launchFragmentInHiltContainer<AddTasksFragment>(Bundle(), R.style.AppTheme) {
            Navigation.setViewNavController(view!!, navController)
        }

        // WHEN - Valid title and description combination and click save
        Espresso.onView(withId(R.id.task_edit_text))
            .perform(ViewActions.replaceText("title"))
        Espresso.onView(withId(R.id.save_task_fab)).perform(ViewActions.click())

        // THEN - Verify that the repository saved the task
        val tasks = (repository.getTasksBlocking() as Result.Success).data
        Assert.assertEquals(tasks.size, 1)
        Assert.assertEquals(tasks[0].title, "title")
    }
}

package com.saiemani.tasks.tasks

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.saiemani.tasks.R
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.Task
import com.saiemani.tasks.data.TasksRepository
import com.saiemani.tasks.di.TasksRepositoryModule
import com.saiemani.tasks.launchFragmentInHiltContainer
import com.saiemani.tasks.util.saveTaskBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import javax.inject.Inject

/**
 * Integration test for the Task List screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@UninstallModules(TasksRepositoryModule::class)
@HiltAndroidTest
class TasksFragmentTest {

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
    fun displayTask_whenRepositoryHasData() {
        // GIVEN - One task already in the repository
        repository.saveTaskBlocking(Task("TITLE1"))

        // WHEN - On startup
        launchActivity()

        // THEN - Verify task is displayed on screen
        Espresso.onView(withText("TITLE1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clearCompletedTasks() {
        // Add one active task and one completed task
        repository.saveTaskBlocking(Task("TITLE1"))
        repository.saveTaskBlocking(Task("TITLE2", true))

        launchActivity()

        // Click clear completed in menu
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Espresso.onView(withText(R.string.str_menu_clear)).perform(ViewActions.click())

        // Verify that only the active task is shown
        Espresso.onView(withText("TITLE1"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText("TITLE2")).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun noTasks_AllTasksFilter_AddTaskViewVisible() {
        launchActivity()

        // Verify the "You have no tasks!" text is shown
        Espresso.onView(withText("You have no tasks!"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(withId(R.id.add_task_fab))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun clickAddTaskButton_navigateToAddEditFragment() {
        // GIVEN - On the home screen
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<TasksFragment>(Bundle(), R.style.AppTheme) {
            Navigation.setViewNavController(this.view!!, navController)
        }

        // WHEN - Click on the "+" button
        Espresso.onView(withId(R.id.add_task_fab)).perform(ViewActions.click())

        // THEN - Verify that we navigate to the add screen
        Mockito.verify(navController).navigate(
            TasksFragmentDirections.actionTasksFragmentToAddTasksFragment()
        )
    }

    private fun launchActivity(): ActivityScenario<MainActivity>? {
        val activityScenario = launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            (activity.findViewById(R.id.tasks_list) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

    private fun checkboxWithText(text: String): Matcher<View> {
        return CoreMatchers.allOf(
            withId(R.id.complete_checkbox),
            ViewMatchers.hasSibling(withText(text))
        )
    }
}

package com.saiemani.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.di.TasksRepositoryModule
import com.saiemani.tasks.tasks.MainActivity
import com.saiemani.tasks.util.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Large End-to-End test for the tasks module
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@UninstallModules(TasksRepositoryModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ITasksRepository

    // An Idling Resource that waits for Data Binding to have no pending bindings
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun createTask() {
        // start up Tasks screen
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Click on the "+" button, add details, and save
        Espresso.onView(withId(R.id.add_task_fab)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.task_edit_text))
            .perform(ViewActions.typeText("title"), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.save_task_fab)).perform(ViewActions.click())

        // Then verify task is displayed on screen
        Espresso.onView(ViewMatchers.withText("title"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }
}

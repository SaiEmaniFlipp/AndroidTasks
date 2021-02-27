package com.saiemani.tasks

import androidx.lifecycle.LiveData
import org.junit.Assert.assertEquals

fun assertSnackbarMessage(snackbarLiveData: LiveData<Event<Int>>, messageId: Int) {
    val value: Event<Int> = snackbarLiveData.getOrAwaitValue()
    assertEquals(value.getContentIfNotHandled(), messageId)
}

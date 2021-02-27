package com.saiemani.tasks

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: ITasksRepository
) : ViewModel() {

    fun saveTask() {

    }
}

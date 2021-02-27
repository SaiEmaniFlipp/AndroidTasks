package com.saiemani.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val repository: ITasksRepository
) : ViewModel() {

    val taskDescription = MutableLiveData<String>()

    private val _taskUpdatedEvent = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> = _taskUpdatedEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    fun saveTask() {
        val currentDescription = taskDescription.value

        if (currentDescription.isNullOrEmpty()) {
            _snackbarText.value = Event(R.string.str_empty_task_message)
            return
        }

        val task = Task(currentDescription)
        createTask(task)
    }

    private fun createTask(newTask: Task) = viewModelScope.launch {
        repository.saveTask(newTask)
        _taskUpdatedEvent.value = Event(Unit)
    }
}

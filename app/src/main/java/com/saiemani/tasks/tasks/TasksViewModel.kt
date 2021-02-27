package com.saiemani.tasks.tasks

import androidx.lifecycle.*
import com.saiemani.tasks.Event
import com.saiemani.tasks.R
import com.saiemani.tasks.data.ITasksRepository
import com.saiemani.tasks.data.Result
import com.saiemani.tasks.data.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: ITasksRepository
) : ViewModel() {

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var resultMessageShown: Boolean = false

    init {
        loadTasks()
    }

    private val _items: LiveData<List<Task>> = repository.observeTasks().distinctUntilChanged().switchMap {
        val result = MutableLiveData<List<Task>>()

        if (it is Result.Success) {
            result.value = it.data
        } else {
            result.value = emptyList()
        }

        return@switchMap result
    }
    val items: LiveData<List<Task>> = _items

    private fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks()
        }
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            repository.completeTask(task.id)
            showSnackbarMessage(R.string.str_task_marked_complete)
        } else {
            repository.activateTask(task.id)
            showSnackbarMessage(R.string.str_task_marked_active)
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            ADD_RESULT_OK -> showSnackbarMessage(R.string.str_successfully_saved_task_message)
        }
        resultMessageShown = true
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
            showSnackbarMessage(R.string.str_completed_tasks_cleared)
        }
    }
}

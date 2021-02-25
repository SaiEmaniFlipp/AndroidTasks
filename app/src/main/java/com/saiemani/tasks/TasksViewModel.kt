package com.saiemani.tasks

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: ITasksRepository
) : ViewModel() {

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
}

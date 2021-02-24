package com.saiemani.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: TasksRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<Task>>()
    val items: LiveData<List<Task>> = _items

}

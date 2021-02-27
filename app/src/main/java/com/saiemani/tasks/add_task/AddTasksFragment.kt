package com.saiemani.tasks.add_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saiemani.tasks.*
import com.saiemani.tasks.databinding.FragmentAddTaskBinding
import com.saiemani.tasks.tasks.ADD_RESULT_OK
import com.saiemani.tasks.util.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTasksFragment: Fragment() {

    private lateinit var viewDataBinding: FragmentAddTaskBinding

    private val viewModel by viewModels<AddTaskViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_task, container, false)
        viewDataBinding = FragmentAddTaskBinding.bind(root).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { it.title = getString(R.string.str_new_task) }

        setupNavigation()
        setupSnackbar()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.taskUpdatedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddTasksFragmentDirections.actionAddTasksFragmentToTasksFragment(
                ADD_RESULT_OK
            )
            findNavController().navigate(action)
        })
    }
}

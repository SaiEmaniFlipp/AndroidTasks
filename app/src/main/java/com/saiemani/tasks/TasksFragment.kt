package com.saiemani.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.saiemani.tasks.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TasksFragment: Fragment() {

    private val tasksViewModel by viewModels<TasksViewModel>()

    private val args: TasksFragmentArgs by navArgs()

    private lateinit var viewDataBinding: FragmentTasksBinding

    private lateinit var listAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = FragmentTasksBinding.inflate(inflater, container, false).apply {
            viewmodel = tasksViewModel
        }
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        activity?.let { it.title = getString(R.string.app_name) }
        setupListAdapter()
        setupFab()
        setupSnackbar()
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let { fab ->
            fab.setOnClickListener { navigateToAddNewTask() }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections.actionTasksFragmentToAddTasksFragment()
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initializes when attempting to set up an adapter")
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, tasksViewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            tasksViewModel.showEditResultMessage(args.userMessage)
        }
    }
}

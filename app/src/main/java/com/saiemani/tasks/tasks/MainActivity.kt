package com.saiemani.tasks.tasks

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saiemani.tasks.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

// Keys for navigation
const val ADD_RESULT_OK = Activity.RESULT_FIRST_USER + 1

package com.example.mytodolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytodolist.ui.main.MainScreen
import com.example.mytodolist.ui.main.viewmodel.MainViewModel
import com.example.mytodolist.ui.main.viewmodel.TodoAndroidViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel: MainViewModel = viewModel(
                factory = TodoAndroidViewModelFactory(application),
            )
            MainScreen(viewModel = viewModel, lifecycleScope)
            LaunchedEffect(key1 = Unit) {
                scope.launch {
                    viewModel.getTodos()
                }
            }
        }
    }
}

package com.example.mytodolist.ui.main.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.mytodolist.data.repository.TodoRepositoryImpl
import com.example.mytodolist.domain.repository.TodoRepository

class TodoAndroidViewModelFactory(
    private val application: Application,
    private val repository: TodoRepository = TodoRepositoryImpl(application),
) :
    ViewModelProvider.AndroidViewModelFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application = application, repository) as T
        }
        return super.create(modelClass, extras)
    }
}

package com.example.mytodolist.ui.main.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.domain.model.Todo
import com.example.mytodolist.domain.repository.TodoRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val application: Application,
    private val todoRepository: TodoRepository,
) : AndroidViewModel(application) {
    private val _items = mutableStateOf(emptyList<Todo>())
    val items: State<List<Todo>> = _items

    private var recentlyDeleteTodo: Todo? = null

    suspend fun getTodos() {
        todoRepository.observeTodos().collectLatest { list ->
            _items.value = list
        }
    }

    fun addTodo(text: String) {
        viewModelScope.launch {
            todoRepository.addTodo(
                Todo(title = text),
            )
            getTodos()
        }
    }

    fun toggle(uid: Int) {
        val todo = _items.value.find { todo -> todo.uid == uid }

        todo?.let {
            viewModelScope.launch {
                todoRepository.updateTodo(
                    it.copy(
                        isDone = !it.isDone,
                    ).apply {
                        this.uid = it.uid
                    },
                )
                getTodos()
            }
        }
    }

    fun delete(uid: Int) {
        val todo = _items.value.find { todo -> todo.uid == uid }
        todo?.let {
            viewModelScope.launch {
                todoRepository.deleteTodo(it)
                recentlyDeleteTodo = it
                getTodos()
            }
        }
    }

    fun restoreTodo() {
        viewModelScope.launch {
            recentlyDeleteTodo?.let { todo ->
                todoRepository.addTodo(todo)
                recentlyDeleteTodo = null
                getTodos()
            }
        }
    }
}

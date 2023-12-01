package com.example.mytodolist.data.repository

import android.app.Application
import androidx.room.Room
import com.example.mytodolist.data.data_source.TodoDatabase
import com.example.mytodolist.domain.model.Todo
import com.example.mytodolist.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(application: Application) : TodoRepository {
    private val db = Room.databaseBuilder(
        context = application,
        klass = TodoDatabase::class.java,
        name = "todo.db",
    ).build()

    override fun observeTodos(): Flow<List<Todo>> {
        return db.todoDao().todos()
    }

    override suspend fun addTodo(todo: Todo) {
        return db.todoDao().insert(todo)
    }

    override suspend fun updateTodo(todo: Todo) {
        return db.todoDao().update(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        return db.todoDao().delete(todo)
    }
}

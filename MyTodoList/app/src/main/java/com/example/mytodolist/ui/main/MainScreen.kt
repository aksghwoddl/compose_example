package com.example.mytodolist.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.mytodolist.R
import com.example.mytodolist.ui.main.components.TodoItem
import com.example.mytodolist.ui.main.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    scope: CoroutineScope,
) {
    var input by rememberSaveable {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val snackBarHostState = SnackbarHostState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "오늘 할 일") })
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),

            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text(text = "할 일") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_24),
                            contentDescription = "add",
                        )
                    },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        viewModel.addTodo(text = input)
                        input = ""
                        keyboardController?.hide()
                    }),
                )

                Divider()
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    content = {
                        items(viewModel.items.value) { todoItem ->
                            Column {
                                TodoItem(
                                    todo = todoItem,
                                    onClick = { todo ->
                                        viewModel.toggle(todo.uid)
                                    },
                                    onDeleteClick = { todo ->
                                        viewModel.delete(todo.uid)
                                        scope.launch {
                                            val result = snackBarHostState.showSnackbar(
                                                message = "할 일 삭제됨",
                                                actionLabel = "취소",
                                                duration = SnackbarDuration.Short,
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.restoreTodo()
                                            }
                                        }
                                    },
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = Color.Black, thickness = 1.dp)
                            }
                        }
                    },
                )
            }
        },
    )
}

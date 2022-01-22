package com.example.templateapp01.ui.favorite

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.templateapp01.model.TodoData
import com.example.templateapp01.ui.components.TodoItem
import com.example.templateapp01.ui.theme.TemplateApp01Theme
import java.util.*

// Stateful Composable that depends on ViewModel.
@Composable
internal fun FavoriteScreen(
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    var titleText by remember { mutableStateOf("") }
    var memoText by remember { mutableStateOf("") }
    var enabledAddButton by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        AddTodoItemContent(
            titleText = titleText,
            memoText = memoText,
            enabledAddButton = enabledAddButton,
            onValueChangeTitleText = {
                titleText = it
                enabledAddButton = memoText.isNotBlank() && titleText.isNotBlank()
            },
            onValueChangeMemoText = {
                memoText = it
                enabledAddButton = memoText.isNotBlank() && titleText.isNotBlank()
            },
            onClickAddTodoItemButton = {
                viewModel.addTodoData(it)
            },
            onClickDeleteAllTodoItemsButton = {
                viewModel.deleteAllTodoItems()
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TodoListContent(
            uiState = viewModel.uiState,
            onClickTodoItem = {
                Toast.makeText(context, "CLick Item id = $it", Toast.LENGTH_LONG).show()
            },
        )
    }
}

// stateless Composable.
@Composable
internal fun AddTodoItemContent(
    titleText: String,
    memoText: String,
    enabledAddButton: Boolean,
    onValueChangeTitleText: (String) -> Unit,
    onValueChangeMemoText: (String) -> Unit,
    onClickAddTodoItemButton: (TodoData) -> Unit,
    onClickDeleteAllTodoItemsButton: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = titleText, onValueChange = onValueChangeTitleText, label = {
            Text(text = "Input Here Todo Title.")
        })
        TextField(value = memoText, onValueChange = onValueChangeMemoText, label = {
            Text(text = "Input Here Todo Memo.")
        })
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElevatedButton(onClick = {
                val todoData =
                    TodoData(title = titleText, memo = memoText, registrationDate = Date())
                onClickAddTodoItemButton(todoData)
            }, enabled = enabledAddButton) {
                Text(text = "Add Todo Item")
            }

            FilledTonalButton(
                onClick = {
                    onClickDeleteAllTodoItemsButton()
                }
            ) {
                Text(text = "Delete All Todo Items")
            }
        }
    }
}

@Composable
internal fun TodoListContent(
    uiState: FavoriteUiState,
    onClickTodoItem: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (val ret = uiState) {
            FavoriteUiState.Initial -> {

            }
            is FavoriteUiState.Error -> {
                Text(text = ret.errorMessage)
            }
            is FavoriteUiState.UpdateTodoList -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    itemsIndexed(ret.todoList) { _, todoData ->
                        TodoItem(
                            todoData = todoData,
                            onClick = {
                                onClickTodoItem(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun AddTodoItemContent_Preview_Button_Disable() {
    TemplateApp01Theme {
        AddTodoItemContent(
            titleText = "",
            memoText = "",
            enabledAddButton = false,
            onValueChangeTitleText = {},
            onValueChangeMemoText = {},
            onClickAddTodoItemButton = {},
            onClickDeleteAllTodoItemsButton = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun AddTodoItemContent_Preview_Button_Enable() {
    TemplateApp01Theme {
        AddTodoItemContent(
            titleText = "",
            memoText = "",
            enabledAddButton = true,
            onValueChangeTitleText = {},
            onValueChangeMemoText = {},
            onClickAddTodoItemButton = {},
            onClickDeleteAllTodoItemsButton = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun TodoListContent_Preview_UpdateTodoList() {
    val todoList = buildList {
        repeat(3) {
            add(
                TodoData(
                    title = "title :$it",
                    memo = "memo: $it",
                    completionDate = Date(),
                    registrationDate = Date()
                )
            )
        }
    }
    TemplateApp01Theme {
        TodoListContent(
            uiState = FavoriteUiState.UpdateTodoList(todoList),
            onClickTodoItem = {},
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun TodoListContent_Preview_Error() {
    TemplateApp01Theme {
        TodoListContent(
            uiState = FavoriteUiState.Error(errorMessage = "error!"),
            onClickTodoItem = {},
        )
    }
}
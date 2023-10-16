package com.example.todocompose.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.todocompose.models.TodoItem
import com.example.todocompose.ui.components.InputField


@Composable
fun MainScreen(
    state: MainScreenState,
    viewModel: MainScreenViewModel,
) {
    InputField(
        onSubmit = {
            viewModel.addItem(
                TodoItem(
                    id = "todo",
                    name = state.inputText,
                    isChecked = false
                )
            )
        }
    )

}

@Preview
@Composable
fun MainScreenPreview(
) {
    MainScreen(
        MainScreenState("inputText", listOf()),
        MainScreenViewModel()
    )
}
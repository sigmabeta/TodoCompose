package com.example.todocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.todocompose.ui.screens.MainScreen
import com.example.todocompose.ui.screens.MainScreenViewModel
import com.example.todocompose.ui.theme.TodoComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainScreenViewModel by viewModels()

        setContent {
            TodoComposeTheme {
                val mainScreenState by viewModel.screenStateFlow.collectAsState()

                MainScreen(
                    state = mainScreenState,
                    viewModel = viewModel,
                )
            }
        }

    }
}

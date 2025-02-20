package com.example.todocompose.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocompose.database.InMemoryRepository
import com.example.todocompose.database.TodoRepository
import com.example.todocompose.database.models.TodoDataRecord
import com.example.todocompose.ui.models.TodoUiItem
import com.example.todocompose.ui.models.asTodoUiItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainScreenViewModel : ViewModel() {
    private val _internalScreenStateFlow =
        MutableStateFlow<MainScreenState>(value = MainScreenState.EMPTY)
    val screenStateFlow: StateFlow<MainScreenState> = _internalScreenStateFlow.asStateFlow()

    private val repository: TodoRepository = InMemoryRepository()


    // init keyword means: run this whenever an instance of this class is constructed
    init {
        viewModelScope.launch {
            repository.dataFlow.collect { records ->
                _internalScreenStateFlow.update { oldValue ->
                    MainScreenState(
                        "",
                        records.map { it.asTodoUiItem() }
                    )
                }
            }
        }
    }

    fun updateNewItemInputText(newText: String) {
        _internalScreenStateFlow.update { oldState ->
            MainScreenState(newText, oldState.toDoListItems)
        }
    }

    fun updateItemText(id: Long, newText: String) {
        _internalScreenStateFlow.update { oldState ->
            val oldListItems = oldState.toDoListItems

            val newListItems = oldListItems
                .map { oldItem ->
                    if (oldItem.id == id) {
                        oldItem.copy(name = newText)
                    } else {
                        oldItem
                    }
                }
            return@update MainScreenState(oldState.newItemInputText, newListItems)
        }
    }

    fun onUpdateItemSubmit(itemToUpdate: TodoUiItem) {
        val oldMatchingItem = repository
            .dataFlow
            .value
            .firstOrNull { it.id == itemToUpdate.id } ?: return

        if (oldMatchingItem.name != itemToUpdate.name) {
            repository.updateItem(itemToUpdate.id, itemToUpdate.name)
        } else {
            toggleIsBeingModified(itemToUpdate)
        }
    }

    fun onAddNewItemButtonClick() {
        val newTodoItem = TodoDataRecord(
            id = Random.nextLong(),
            name = _internalScreenStateFlow.value.newItemInputText,
            completed = false
        )
        if (newTodoItem.name == "") {
            return
        } else {
            repository.addItem(newTodoItem)
        }
    }

    fun onDeleteButtonClick(itemToDelete: TodoUiItem) {
        repository.deleteItem(itemToDelete.id)
    }

    fun onEditButtonClick(itemToUpdate: TodoUiItem) {
        _internalScreenStateFlow.update { oldState ->
            val oldListItems = oldState.toDoListItems

            val newListItems = oldListItems
                .map { oldItem ->
                    if (oldItem.id == itemToUpdate.id) {
                        oldItem.copy(isBeingModified = true)
                    } else {
                        oldItem
                    }
                }

            return@update MainScreenState(oldState.newItemInputText, newListItems)
        }
    }

    fun toggleChecked(itemToChange: TodoUiItem) {
        repository.toggleCompleted(itemToChange.id)
    }

    private fun toggleIsBeingModified(itemToUpdate: TodoUiItem) {
        _internalScreenStateFlow.update { oldState ->
            val oldListItems = oldState.toDoListItems

            val newListItems = oldListItems
                .map { oldItem ->
                    if (oldItem.id == itemToUpdate.id) {
                        oldItem.copy(isBeingModified = false)
                    } else {
                        oldItem
                    }
                }

            return@update MainScreenState(oldState.newItemInputText, newListItems)
        }
    }
}
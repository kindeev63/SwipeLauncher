package com.knomster.navigation_component

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationComponent<T>(val startDestination: T) {

    private val _backStack = MutableStateFlow<List<T>>(listOf(startDestination))
    val backStack: StateFlow<List<T>>
        get() = _backStack.asStateFlow()

    fun addToBackStack(screen: T) {
        changeBackStack { it.add(screen) }
    }

    fun popUpBackStack() {
        changeBackStack { it.removeLastOrNull() }
    }

    fun clearBackStack() {
        _backStack.value = listOf(startDestination)
    }

    fun removeFromBackStack(screen: T) {
        changeBackStack { it.remove(screen) }
    }

    fun changeBackStack(operation: (MutableList<T>) -> Unit) {
        _backStack.value = backStack.value.toMutableList().apply(operation)
    }
}
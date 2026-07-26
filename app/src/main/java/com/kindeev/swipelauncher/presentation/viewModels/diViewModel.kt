package com.kindeev.swipelauncher.presentation.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kindeev.swipelauncher.presentation.DI

@Composable
inline fun <reified T: ViewModel> diViewModel(
    savedStateHandle: SavedStateHandle? = null
): T {
    val creator = DI.container.getViewModelCreator<T>()

    val factory = viewModelFactory {
        addInitializer(T::class) { creator(savedStateHandle ?: this.createSavedStateHandle()) }
    }

    return viewModel(factory = factory)
}
package com.kindeev.swipelauncher.presentation.viewModels

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kindeev.swipelauncher.presentation.DI
import com.knomster.di.DIParameters
import com.knomster.di.InsertParameters

@Composable
inline fun <reified T: ViewModel> diViewModel(insertParameters: (InsertParameters) -> Unit = {}): T {
    val diParameters = DIParameters()
    insertParameters(diParameters)
    val creator = DI.getViewModelCreator<T>()

    val factory = viewModelFactory {
        addInitializer(T::class) { creator(diParameters) }
    }

    return viewModel(factory = factory)
}
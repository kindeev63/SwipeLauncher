package com.kindeev.swipelauncher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllCircleMenusScreenViewModelFactory(private val mainAppViewModel: MainAppViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllCircleMenusScreenViewModel(mainAppViewModel) as T
    }
}
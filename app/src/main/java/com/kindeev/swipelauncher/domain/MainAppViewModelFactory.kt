package com.kindeev.swipelauncher.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainAppViewModelFactory(private val appDao: AppDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainAppViewModel(appDao = appDao) as T
    }
}
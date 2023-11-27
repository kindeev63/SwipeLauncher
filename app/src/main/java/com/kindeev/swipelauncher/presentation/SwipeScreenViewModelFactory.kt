package com.kindeev.swipelauncher.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SwipeScreenViewModelFactory(private val context: Context, private val mainAppViewModel: MainAppViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SwipeScreenViewModel(context, mainAppViewModel) as T
    }
}
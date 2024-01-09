package com.kindeev.swipelauncher.presentation.viewModels.factories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.viewModels.SwipeScreenViewModel

class SwipeScreenViewModelFactory(private val context: Context, private val mainAppViewModel: MainAppViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SwipeScreenViewModel(context, mainAppViewModel) as T
    }
}
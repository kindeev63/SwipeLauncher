package com.kindeev.swipelauncher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditCircleMenuScreenViewModelFactory(
    private val mainAppViewModel: MainAppViewModel,
    private val circleMenuId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditCircleMenuScreenViewModel(
            mainAppViewModel = mainAppViewModel,
            circleMenuId = circleMenuId
        ) as T
    }
}
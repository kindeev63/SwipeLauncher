package com.kindeev.swipelauncher.presentation.viewModels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.viewModels.EditCircleMenuScreenViewModel

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
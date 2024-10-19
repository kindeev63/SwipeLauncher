package com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditCircleMenuScreenVMFactory(private val circleMenuId: Int?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditCircleMenuScreenVM(
            circleMenuId = circleMenuId
        ) as T
    }
}
package com.kindeev.swipelauncher.domain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditCircleMenuScreenVMFactory(private val circleMenuId: Int?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditCircleMenuScreenVM(
            circleMenuId = circleMenuId
        ) as T
    }
}
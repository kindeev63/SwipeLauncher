package com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

class EditCircleMenuScreenVMFactory(
    private val mainAppVM: MainAppVM,
    private val circleMenuId: Int?,
    private val newCircleMenuTitle: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditCircleMenuScreenVM(
            mainAppVM = mainAppVM,
            circleMenuId = circleMenuId,
            newCircleMenuTitle = newCircleMenuTitle
        ) as T
    }
}
package com.kindeev.swipelauncher.domain.viewModels.AllCircleMenusScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

class AllCircleMenusScreenVMFactory(private val mainAppVM: MainAppVM): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllCircleMenusScreenVM(mainAppVM) as T
    }
}
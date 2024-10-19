package com.kindeev.swipelauncher.domain.viewModels.screens.allCircleMenus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllCircleMenusVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AllCircleMenusVM(context) as T
    }
}
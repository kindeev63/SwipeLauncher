package com.kindeev.swipelauncher.presentation.viewModels.allCircleMenus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AllCircleMenusScreenVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AllCircleMenusScreenVM(context) as T
    }
}
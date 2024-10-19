package com.kindeev.swipelauncher.domain.viewModels.screens.hiddenAppsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HiddenAppsScreenVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HiddenAppsScreenVM(context) as T
    }
}
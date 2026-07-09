package com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainSettingsScreenVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainSettingsScreenVM(context) as T
    }
}
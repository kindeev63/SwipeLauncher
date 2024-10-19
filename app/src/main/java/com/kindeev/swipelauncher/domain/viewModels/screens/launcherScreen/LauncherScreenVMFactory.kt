package com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LauncherScreenVMFactory(
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LauncherScreenVM(context) as T
    }
}
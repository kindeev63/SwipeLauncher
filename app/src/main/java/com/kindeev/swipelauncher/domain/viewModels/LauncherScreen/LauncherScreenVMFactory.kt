package com.kindeev.swipelauncher.domain.viewModels.LauncherScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

class LauncherScreenVMFactory(
    private val mainAppVM: MainAppVM,
    private val context: Context
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LauncherScreenVM(mainAppVM, context) as T
    }
}
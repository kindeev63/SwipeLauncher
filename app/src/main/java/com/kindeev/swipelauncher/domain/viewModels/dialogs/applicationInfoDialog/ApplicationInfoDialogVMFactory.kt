package com.kindeev.swipelauncher.domain.viewModels.dialogs.applicationInfoDialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ApplicationInfoDialogVMFactory(private val context: Context, private val packageName: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ApplicationInfoDialogVM(context, packageName) as T
    }
}
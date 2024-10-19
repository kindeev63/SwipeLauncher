package com.kindeev.swipelauncher.domain.viewModels.elements.openAppDataItem

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OpenAppDataItemVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return OpenAppDataItemVM(context) as T
    }
}
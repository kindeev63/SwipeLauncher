package com.kindeev.swipelauncher.domain.viewModels.dialogs.imageDialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageDialogVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("CAST_NEVER_SUCCEEDS")
        return ImageDialogVM(context) as T
    }
}
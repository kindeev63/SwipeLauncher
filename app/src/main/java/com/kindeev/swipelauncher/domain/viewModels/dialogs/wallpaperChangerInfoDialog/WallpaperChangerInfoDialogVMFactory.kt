package com.kindeev.swipelauncher.domain.viewModels.dialogs.wallpaperChangerInfoDialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WallpaperChangerInfoDialogVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WallpaperChangerInfoDialogVM(context) as T
    }
}
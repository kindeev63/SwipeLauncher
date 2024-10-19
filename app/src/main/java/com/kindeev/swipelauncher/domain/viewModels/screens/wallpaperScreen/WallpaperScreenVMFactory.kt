package com.kindeev.swipelauncher.domain.viewModels.screens.wallpaperScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WallpaperScreenVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return WallpaperScreenVM(context) as T
    }
}
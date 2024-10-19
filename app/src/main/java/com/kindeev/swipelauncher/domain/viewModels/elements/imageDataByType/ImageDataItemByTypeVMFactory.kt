package com.kindeev.swipelauncher.domain.viewModels.elements.imageDataByType

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImageDataItemByTypeVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ImageDataItemByTypeVM(context) as T
    }
}
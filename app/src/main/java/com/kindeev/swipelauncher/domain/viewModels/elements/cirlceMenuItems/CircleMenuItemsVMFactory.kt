package com.kindeev.swipelauncher.domain.viewModels.elements.cirlceMenuItems

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CircleMenuItemsVMFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CircleMenuItemsVM(context) as T
    }
}
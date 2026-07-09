package com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditCircleMenuVMFactory(
    private val circleMenuId: Int?,
    private val size: Float,
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditCircleMenuScreenVM(circleMenuId, size, context) as T
    }
}
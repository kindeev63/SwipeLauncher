package com.kindeev.swipelauncher.domain.viewModels.elements.openAppDataItem

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase

class OpenAppDataItemVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)

    fun getApplicationInfo(packageName: String): ApplicationInfo {
        return applicationsUseCase.getApplicationInfo(packageName)
    }
}
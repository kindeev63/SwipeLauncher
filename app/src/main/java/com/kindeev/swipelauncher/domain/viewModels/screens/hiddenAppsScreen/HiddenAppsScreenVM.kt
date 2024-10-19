package com.kindeev.swipelauncher.domain.viewModels.screens.hiddenAppsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase

class HiddenAppsScreenVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)

    fun getHiddenApps(applicationsInfo: List<ApplicationInfo>): List<ApplicationInfo> {
        return applicationsUseCase.getHidden(applicationsInfo)
    }
}
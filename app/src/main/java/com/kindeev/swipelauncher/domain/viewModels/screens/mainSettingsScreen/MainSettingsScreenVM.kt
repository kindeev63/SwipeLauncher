package com.kindeev.swipelauncher.domain.viewModels.screens.mainSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase

class MainSettingsScreenVM(context: Context): ViewModel() {
    private val applicationsUseCase = ApplicationsUseCase(context)

    fun getApplicationInfo(packageName: String): ApplicationInfo {
        return applicationsUseCase.getApplicationInfo(packageName)
    }

    fun getAllApplicationsData(applicationsInfo: List<ApplicationInfo>): List<ApplicationData> {
        return applicationsUseCase.getAllApplicationData(applicationsInfo)
    }
}
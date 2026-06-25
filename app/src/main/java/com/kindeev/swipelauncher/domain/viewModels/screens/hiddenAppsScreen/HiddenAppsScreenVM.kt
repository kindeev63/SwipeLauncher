package com.kindeev.swipelauncher.domain.viewModels.screens.hiddenAppsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import kotlinx.coroutines.launch

class HiddenAppsScreenVM(context: Context) : ViewModel() {
    private val applicationsUseCase = ApplicationsUseCase(context)

    fun getHiddenApps(
        applicationsInfo: List<ApplicationInfo>,
        applicationData: List<ApplicationData>
    ): List<ApplicationData> {
        return applicationsUseCase.getAllApplicationData(
            applicationsUseCase.getHidden(
                applicationsInfo = applicationsInfo,
                applicationData = applicationData
            )
        )
    }

    fun showApp(packageName: String) {
        viewModelScope.launch {
            applicationsUseCase.showApp(packageName)
        }
    }
}
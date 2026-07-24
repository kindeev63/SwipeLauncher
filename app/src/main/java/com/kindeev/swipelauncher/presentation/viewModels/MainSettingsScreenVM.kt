package com.kindeev.swipelauncher.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase

class MainSettingsScreenVM(
    private val applicationsManager: ApplicationsManager,
    val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    val dataRepository: DataRepository
): ViewModel() {

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return applicationsManager.getApplication(packageName)
    }

}
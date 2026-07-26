package com.kindeev.swipelauncher.presentation.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class MainSettingsScreenVM(
    private val applicationsManager: ApplicationsManager,
    val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.getStateFlow<CircleMenuAction?>("pickedAction", null).filterNotNull()
                .collect { action ->
                    changeClickOnClockAction(action)
                    savedStateHandle["pickedAction"] = null
                }
        }
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return applicationsManager.getApplication(packageName)
    }

    fun changeClickOnClockAction(action: CircleMenuAction) = viewModelScope.launch {
        val settings = settingsStateFlowUseCase.settings.value
        dataRepository.insertSettings(
            settings.copy(
                clickOnClock = settings.clickOnClock.copy(
                    action = action
                )
            )
        )
    }

}
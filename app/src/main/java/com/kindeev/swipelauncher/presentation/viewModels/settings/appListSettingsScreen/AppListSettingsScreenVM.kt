package com.kindeev.swipelauncher.presentation.viewModels.settings.appListSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.viewModels.settings.appListSettingsScreen.entities.AppListSettingCategory
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppListSettingsScreenVM(
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    private val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    private val dataRepository: DataRepository,
    context: Context
): ViewModel() {
    val settingsList = settingsStateFlowUseCase.settings.map { settings ->
        listOf<SettingsListItem<AppListSettingCategory>>(
            SettingsListItem.Switch(
                id = AppListSettingCategory.OpenLastApp,
                title = context.resources.getString(R.string.open_last_app_title),
                description = context.resources.getString(R.string.open_last_app_description),
                iconUnicode = "\ue913",
                checked = settings.openLastApp,
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun switch(id: AppListSettingCategory, value: Boolean) {
        when (id) {
            AppListSettingCategory.OpenLastApp -> viewModelScope.launch(Dispatchers.IO) {
                val settings = settingsStateFlowUseCase.settings.value
                dataRepository.insertSettings(
                    settings.copy(
                        openLastApp = value
                    )
                )
            }
        }
    }

    fun onBackPressed() {
        navigationComponent.popUpBackStack()
    }
}
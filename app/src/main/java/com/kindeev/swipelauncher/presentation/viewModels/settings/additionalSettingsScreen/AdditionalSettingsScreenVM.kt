package com.kindeev.swipelauncher.presentation.viewModels.settings.additionalSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.viewModels.settings.additionalSettingsScreen.entities.AdditionalSettingsCategory
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdditionalSettingsScreenVM(
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    private val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    private val dataRepository: DataRepository,
    context: Context
): ViewModel() {

    val settingsList = settingsStateFlowUseCase.settings.map { settings ->
        listOf<SettingsListItem<AdditionalSettingsCategory>>(
            SettingsListItem.Switch(
                id = AdditionalSettingsCategory.OpenAppActionWithImage,
                title = context.getString(R.string.open_app_action_with_image_title),
                description = context.getString(R.string.open_app_action_with_image_description),
                iconUnicode = "\uef40",
                checked = settings.pickAppActionWithImage
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun switch(id: AdditionalSettingsCategory, value: Boolean) {
        when (id) {
            AdditionalSettingsCategory.OpenAppActionWithImage -> viewModelScope.launch(Dispatchers.IO) {
                val settings = settingsStateFlowUseCase.settings.value
                dataRepository.insertSettings(
                    settings.copy(
                        pickAppActionWithImage = value
                    )
                )
            }
        }
    }

    fun onBackPressed() {
        navigationComponent.popUpBackStackSafe()
    }
}

package com.kindeev.swipelauncher.presentation.viewModels.settings.launcherSettingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.useCases.ActionItemDataUseCase
import com.kindeev.swipelauncher.presentation.viewModels.settings.launcherSettingsScreen.entities.LauncherSettingsCategory
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherSettingsScreenVM(
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    private val dataRepository: DataRepository,
    private val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    private val actionItemDataUseCase: ActionItemDataUseCase,
    context: Context
): ViewModel() {

    val settingsList = settingsStateFlowUseCase.settings.map { settings ->
        listOf(
            SettingsListItem.SwitchWithAction(
                id = LauncherSettingsCategory.ClickOnClock,
                title = context.getString(R.string.click_on_clock_title),
                description = context.getString(R.string.click_on_clock_description),
                iconUnicode = "\uea5d",
                checked = settings.clickOnClock.enable,
                actionItemData = actionItemDataUseCase.getActionItem(settings.clickOnClock.action)
            ),
            SettingsListItem.Switch(
                id = LauncherSettingsCategory.BlackTextColorOnWallpaper,
                title = context.getString(R.string.black_text_color_title),
                description = context.getString(R.string.black_text_color_description),
                iconUnicode = "\ue40a",
                checked = settings.blackTextColorOnWallpaper
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun switch(id: LauncherSettingsCategory, value: Boolean) {
        when (id) {
            LauncherSettingsCategory.ClickOnClock -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val settings = settingsStateFlowUseCase.settings.value
                    dataRepository.insertSettings(
                        settings.copy(
                            clickOnClock = settings.clickOnClock.copy(enable = value)
                        )
                    )
                }
            }

            LauncherSettingsCategory.BlackTextColorOnWallpaper -> {
                viewModelScope.launch(Dispatchers.IO) { 
                    val settings = settingsStateFlowUseCase.settings.value
                    dataRepository.insertSettings(
                        settings.copy(
                            blackTextColorOnWallpaper = value
                        )
                    )
                }
            }
        }
    }

    fun changeClickOnClockAction(action: CircleMenuAction) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun pickActionForClickOnClock() {
        navigationComponent.addToBackStack(
            SettingsActivityNav.ActionDialog(
                onPick = ::changeClickOnClockAction
            )
        )
    }

    fun onBackPressed() {
        navigationComponent.popUpBackStackSafe()
    }
}
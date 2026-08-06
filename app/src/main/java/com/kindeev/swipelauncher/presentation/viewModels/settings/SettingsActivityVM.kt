package com.kindeev.swipelauncher.presentation.viewModels.settings

import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.knomster.navigation_component.NavigationComponent

class SettingsActivityVM(
    private val navigationComponent: NavigationComponent<SettingsActivityNav>
): ViewModel() {
    val navigationBackStack = navigationComponent.backStack

    fun navigationOnBack() {
        navigationComponent.popUpBackStack()
    }
}
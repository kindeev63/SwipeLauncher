package com.kindeev.swipelauncher.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.navigation.SettingsActivityNav
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.launch
import kotlin.collections.filterNotNull

class MainSettingsScreenVM(
    private val applicationsManager: ApplicationsManager,
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    private val circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    private val circleMenuImageToImageBitmap: CircleMenuImageToImageBitmap,
    private val navigationComponent: NavigationComponent<SettingsActivityNav>,
    val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    val dataRepository: DataRepository,
) : ViewModel() {

    private val menuSize = Constants.minScreenLength / 6f - 10

    fun getCircleMenuToDraw(id: Int): CircleMenuToDraw? {
        val imageMapper = circleMenuImageToImageBitmap.mapper.value
        return circleMenuStateFlowUseCase.circleMenus.value.find { it.id == id }?.let { menu ->
            val parameters =
                circleMenuParametersUseCase.getParametersGenerator(menu.items.size)(menuSize)
            CircleMenuToDraw(
                id = menu.id,
                title = menu.title,
                menuSize = menuSize,
                itemSize = parameters.itemSize,
                items = menu.items.mapIndexed { index, item ->
                    parameters.offsets[index]?.let { offset ->
                        imageMapper[item.image]?.let { imageBitmap ->
                            CircleMenuItemToDraw(
                                offset = offset,
                                imageBitmap = imageBitmap
                            )
                        }
                    }
                }.filterNotNull()
            )
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

    fun navigateToAllCircleMenus() {
        navigationComponent.addToBackStack(SettingsActivityNav.CircleMenus)
    }

    fun navigateToTutorial() {
        navigationComponent.addToBackStack(SettingsActivityNav.Tutorial)
    }

    fun openActionDialog() {
        navigationComponent.addToBackStack(
            SettingsActivityNav.ActionDialog(
                onPick = { action ->
                    changeClickOnClockAction(action)
                }
            )
        )
    }
}
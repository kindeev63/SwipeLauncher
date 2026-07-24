package com.kindeev.swipelauncher.presentation.viewModels.launcherScreen

import android.content.Context
import android.os.Vibrator
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenUrlAction
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities.CircleMenuWithOffset
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.FlashLightUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenUrlUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.TelephoneUseCase
import com.kindeev.swipelauncher.presentation.useCases.stateFlows.CircleMenuForUIStateFlowUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.useCases.GetSystemServiceUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.mappers.toDraw
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.mappers.toDrawVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.pow

class LauncherScreenVM(
    private val telephoneUseCase: TelephoneUseCase,
    private val openSettingsUseCase: OpenSettingsUseCase,
    private val flashLightUseCase: FlashLightUseCase,
    private val openUrlUseCase: OpenUrlUseCase,
    private val density: Float,
    private val applicationsManager: ApplicationsManager,
    val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    val openAppUseCase: OpenAppUseCase,
    getSystemServiceUseCase: GetSystemServiceUseCase,
    circleMenuForUIStateFlowUseCase: CircleMenuForUIStateFlowUseCase,
) : ViewModel() {

    private val menuSize = Constants.minScreenLength / 3f * 2

    private val circleMenusToDrawVM = circleMenuForUIStateFlowUseCase.circleMenusForUI.map { it.toDrawVM(menuSize) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    private val currentMenuId = MutableStateFlow(0)

    private val currentMenu = currentMenuId.combine(circleMenusToDrawVM) { id, menus ->
        menus[id]
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private val currentMenuOffset = MutableStateFlow<Offset?>(null)

    val currentMenuWithOffset = currentMenu.combine(currentMenuOffset) { menu, offset ->
        menu?.let {
            offset?.let {
                CircleMenuWithOffset(menu.toDraw(), offset)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    private val vibrator =
        getSystemServiceUseCase.get(Context.VIBRATOR_SERVICE) as Vibrator

    private val radiusSq = (menuSize * 0.3).pow(2)

    private var clickTime = 0L
    private var actionInProgress = false

    private val _screenState = MutableStateFlow(LauncherScreenState.SwipeBox)
    val screenState: StateFlow<LauncherScreenState> = _screenState

    fun closeSearchBox() {
        _screenState.value = LauncherScreenState.SwipeBox
        clearSearch()
    }

    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        val offset = Offset(
            x = event.x / density,
            y = event.y / density
        )
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.eventTime - clickTime < 300L) {
                    // Double click
                    _searchText.value = TextFieldValue("")
                    _screenState.value = LauncherScreenState.SearchBox
                } else {
                    currentMenuOffset.value = offset
                }
                clickTime = event.eventTime
                actionInProgress = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!actionInProgress) {
                    actionInProgress = true
                    currentMenuOffset.value?.let { menuOffset ->
                        val index = getElementIndexOnCords(
                            offset = Offset(
                                x = offset.x - menuOffset.x,
                                y = offset.y - menuOffset.y,
                            )
                        )
                        index?.let {
                            currentMenu.value?.items?.getOrNull(index)?.let { item ->
                                viewModelScope.launch {
                                    executeAction(item.action, offset)
                                }
                            }
                        }
                    }
                    actionInProgress = false
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                currentMenuId.value = 0
                currentMenuOffset.value = null
            }
        }
        true
    }

    fun executeAction(
        action: CircleMenuAction,
        offset: Offset? = null
    ) {
        when (action) {

            is OpenCircleMenuAction -> {
                offset?.let { newOffset ->
                    currentMenuId.value = action.id
                    currentMenuOffset.value = newOffset
                    vibrator.vibrate(20)
                }
            }

            is OpenSettingsAction -> {
                openSettingsUseCase()
            }

            is OpenAppAction -> {
                applicationsManager.open(action.packageName)
            }

            is FlashLightOnAction -> {
                viewModelScope.launch {
                    flashLightUseCase.on()
                }
            }

            is FlashLightOffAction -> {
                viewModelScope.launch {
                    flashLightUseCase.off()
                }
            }

            is ChangeFlashLightConditionAction -> {
                viewModelScope.launch {
                    when (flashLightUseCase.flashLightState) {
                        FlashLightUseCase.FlashLightState.On -> flashLightUseCase.off()
                        FlashLightUseCase.FlashLightState.Off -> flashLightUseCase.on()
                    }
                }
            }

            is CallAction -> {
                telephoneUseCase.call(action.phoneNumber)
            }

            is DialAction -> {
                telephoneUseCase.dial(action.phoneNumber)
            }

            is OpenUrlAction -> {
                openUrlUseCase.open(action.url)
            }
        }
    }

    private fun getElementIndexOnCords(
        offset: Offset
    ): Int? {
        if (offset.x.pow(2) + offset.y.pow(2) > radiusSq) {
            val angles = currentMenu.value?.angles
            if (angles?.isEmpty() ?: true) {
                return 0
            }
            val currentAngle = if (offset.y == 0f) {
                if (offset.x > 0) 90f else 270f
            } else {
                offset.getAngle((atan(offset.x / offset.y) / PI * 180f).toFloat())
            }
            angles.forEachIndexed { index, angle ->
                if (currentAngle < angle) return index
            }
            return 0
        }
        return null
    }

    private fun Offset.getAngle(angle: Float): Float {
        return if (y > 0) {
            180 - angle
        } else {
            if (angle > 0) {
                360 - angle
            } else {
                -angle
            }
        }
    }


// Search Box

    private val _searchText = MutableStateFlow(TextFieldValue(""))
    val searchText: StateFlow<TextFieldValue> = _searchText.asStateFlow()

    val searchResults =
        _searchText.combine(applicationsManager.applications) { search, applications ->
            applications
                .filter {
                    it.title
                        .lowercase()
                        .contains(search.text.lowercase().trim())
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            searchResults.collect { applications ->
                if (settingsStateFlowUseCase.settings.value.openLastApp && searchText.value.text.firstOrNull() != ' ' && applications.size == 1) {
                    _searchText.value = TextFieldValue("")
                    val app = applications.first()
                    openAppUseCase.open(app.packageName)
                    closeSearchBox()
                }
            }
        }
    }


    fun search(value: TextFieldValue) {
        _searchText.value = value
    }

    fun clearSearch() {
        _searchText.value = TextFieldValue("")
    }

// ApplicationInfoDialog

    fun getAppDetails(packageName: String) {
        applicationsManager.openAppDetails(packageName)
    }

    fun deleteApp(packageName: String) {
        applicationsManager.delete(packageName)
    }

}
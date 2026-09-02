package com.kindeev.swipelauncher.presentation.viewModels.launcherScreen

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
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.EmptyAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenUrlAction
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities.CircleMenuWithOffset
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.useCases.VibrateUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.FlashLightUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenUrlUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.TelephoneUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuItemIndexOnCordsUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.kindeev.swipelauncher.presentation.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.corsOutRadiusGenerator
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.getSwipeRadius
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherScreenVM(
    private val telephoneUseCase: TelephoneUseCase,
    private val openSettingsUseCase: OpenSettingsUseCase,
    private val flashLightUseCase: FlashLightUseCase,
    private val openUrlUseCase: OpenUrlUseCase,
    private val density: Float,
    private val applicationsManager: ApplicationsManager,
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    private val circleMenuItemIndexOnCordsUseCase: CircleMenuItemIndexOnCordsUseCase,
    val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    val openAppUseCase: OpenAppUseCase,
    circleMenuImageToImageBitmap: CircleMenuImageToImageBitmap,
    private val vibrateUseCase: VibrateUseCase,
    circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase
) : ViewModel() {

    private val menuSize = Constants.minScreenLength / 3f * 2
    private val cordsOutRadius = corsOutRadiusGenerator(menuSize, ::getSwipeRadius)

    private val currentMenuId = MutableStateFlow(0)

    private val currentMenu =
        currentMenuId.combine(circleMenuStateFlowUseCase.circleMenus) { id, menus ->
            menus.getOrNull(id)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val itemIndexOnCords = currentMenu.map { menu ->
        if (menu == null) {
            { _ -> 0 }
        } else {
            circleMenuItemIndexOnCordsUseCase.getItemIndexOnCordsGenerator(menu.items.size)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = { _ -> 0 }
    )

    private val currentMenuOffset = MutableStateFlow<Offset?>(null)

    val currentMenuWithOffset = combine(
        currentMenu,
        circleMenuImageToImageBitmap.mapper,
        currentMenuOffset
    ) { menu, imageMapper, offset ->
        if (menu == null || offset == null) return@combine null
        val parameters =
            circleMenuParametersUseCase.getParametersGenerator(menu.items.size)(menuSize)
        CircleMenuWithOffset(
            offset = offset,
            circleMenuToDraw =
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
        )
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )


    private var clickTime = 0L

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
            }

            MotionEvent.ACTION_MOVE -> {
                currentMenuOffset.value?.let { menuOffset ->
                    val swipeOffset = Offset(
                        x = offset.x - menuOffset.x,
                        y = offset.y - menuOffset.y,
                    )
                    if (cordsOutRadius(swipeOffset)) {
                        val index = itemIndexOnCords.value(swipeOffset)
                        currentMenu.value?.items?.getOrNull(index)?.let { item ->
                            executeAction(item.action, offset)
                        }
                    }
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
        viewModelScope.launch {
            when (action) {
                is OpenCircleMenuAction -> {
                    offset?.let { newOffset ->
                        currentMenuId.value = action.id
                        currentMenuOffset.value = newOffset
                        vibrateUseCase.vibrate()
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

                EmptyAction -> {}
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

    fun pickFirstItem() {
        searchResults.value.firstOrNull()?.let { application ->
            openAppUseCase.open(application.packageName)
            _searchText.value = TextFieldValue("")
            closeSearchBox()
        }
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
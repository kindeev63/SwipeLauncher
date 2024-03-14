package com.kindeev.swipelauncher.domain.viewModels.LauncherScreen

import android.content.Context
import android.os.Vibrator
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.CircleMenuDirection
import com.kindeev.swipelauncher.domain.DataObject.getAs
import com.kindeev.swipelauncher.domain.dataBase.MenuImages
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.CircleMenuOffset
import com.kindeev.swipelauncher.domain.useCases.DeleteAppUseCase
import com.kindeev.swipelauncher.domain.useCases.FilterAllAppsToSearchBoxUseCase
import com.kindeev.swipelauncher.domain.useCases.FlashLightUseCase
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.domain.useCases.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

class LauncherScreenVM(
    val mainAppVM: MainAppVM,
    context: Context
) : ViewModel() {
    private val _circleMenuOffset = MutableLiveData<CircleMenuOffset?>(null)
    val circleMenuOffset: LiveData<CircleMenuOffset?> = _circleMenuOffset
    private val _menuImages = MutableLiveData<MenuImages>()
    val menuImages: LiveData<MenuImages> = _menuImages
    private val _circleMenu = MutableLiveData<CircleMenu>()
    val circleMenu: LiveData<CircleMenu> = _circleMenu
    private val _screenState = MutableLiveData(LauncherScreenState.SwipeBox)
    val screenState: LiveData<LauncherScreenState> = _screenState
    private val _searchText = MutableLiveData("")
    val searchText: LiveData<String> = _searchText



    val menuSize = Integer.min(
        context.resources.configuration.screenWidthDp,
        context.resources.configuration.screenHeightDp
    ) / 3 * 2f
    private val density = context.resources.displayMetrics.density
    private var clickTime = 0L
    private val checkCircleMenuUseCase = CheckCircleMenuUseCase(mainAppVM, context)
    private val openSettingsUseCase = OpenSettingsUseCase(context)
    private val openAppUseCase = OpenAppUseCase(context)
    val deleteAppUseCase = DeleteAppUseCase(context)
    val filterAllAppsToSearchBoxUseCase = FilterAllAppsToSearchBoxUseCase(context)
    private val flashLightUseCase = FlashLightUseCase(context)
    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val packageName = context.packageName

    fun centerCircleCenterOffset(): Offset {
        circleMenuOffset.value?.let { offset ->
            val x = (offset.swipe.x - offset.start.x) * density
            val y = (offset.swipe.y - offset.start.y) * density
            val boarderOffset = (menuSize / 2) * density
            return Offset(
                x = if (x > boarderOffset) {
                    boarderOffset * 2f
                } else if (x < -boarderOffset) {
                    0f
                } else x + boarderOffset,
                y = if (y > boarderOffset) {
                    boarderOffset * 2f
                } else if (y < -boarderOffset) {
                    0f
                } else y + boarderOffset,
            )
        }
        return Offset.Zero
    }

    fun search(searchText: String) {
        _searchText.value = searchText
    }

    fun selectSearchElement(packageName: String) {
        if (packageName == this.packageName) {
            openSettingsUseCase.invoke()
        } else {
            openAppUseCase.invoke(packageName)
        }
        closeSearchBox()
    }

    fun setCircleMenu(circleMenu: CircleMenu?) {
        _circleMenu.value = circleMenu
        circleMenu?.menuImages?.let { _menuImages.value = it }
    }
    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.eventTime - clickTime < 300L) {

                    // Double click
                    _searchText.value = ""
                    _screenState.value = LauncherScreenState.SearchBox
                } else {
                    val offset = Offset(
                        x = event.x / density,
                        y = event.y / density
                    )
                    _circleMenuOffset.value = CircleMenuOffset(
                        start = offset,
                        swipe = offset
                    )
                }
                clickTime = event.eventTime
            }

            MotionEvent.ACTION_MOVE -> {
                _circleMenuOffset.value?.let { notNullMenuOffset ->
                    val direction = getItemDirection(
                        menuSize = menuSize,
                        cordsX = notNullMenuOffset.swipe.x - notNullMenuOffset.start.x,
                        cordsY = notNullMenuOffset.swipe.y - notNullMenuOffset.start.y
                    )
                    when (direction) {
                        null -> {
                            _circleMenuOffset.value = _circleMenuOffset.value?.copy(
                                swipe = Offset(
                                    x = event.x / density,
                                    y = event.y / density
                                )
                            )
                        }

                        else -> {
                            circleMenu.value?.let { circleMenu ->
                                executeAction(
                                    action = when (direction) {
                                        CircleMenuDirection.Up -> {
                                            circleMenu.menuActions.upAction
                                        }

                                        CircleMenuDirection.Down -> {
                                            circleMenu.menuActions.downAction
                                        }

                                        CircleMenuDirection.Right -> {
                                            circleMenu.menuActions.rightAction
                                        }

                                        CircleMenuDirection.Left -> {
                                            circleMenu.menuActions.leftAction
                                        }
                                    },
                                    direction = direction
                                )
                            }
                        }
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                setCircleMenu(mainAppVM.allCircleMenu.value?.find { it.id == 0 })
                _circleMenuOffset.value = null
            }
        }
        true
    }

    fun closeSearchBox() {
        _screenState.value = LauncherScreenState.SwipeBox
    }

    private fun getItemDirection(
        menuSize: Float,
        cordsX: Float,
        cordsY: Float,
    ): CircleMenuDirection? {
        val border = object {
            val big = menuSize / 2 - (menuSize / 6 + menuSize / 10)
            val small = menuSize / 10
        }
        if (cordsY <= -border.big && -border.small <= cordsX && cordsX <= border.small) return CircleMenuDirection.Up
        if (cordsY >= border.big && -border.small <= cordsX && cordsX <= border.small) return CircleMenuDirection.Down
        if (cordsX >= border.big && -border.small <= cordsY && cordsY <= border.small) return CircleMenuDirection.Right
        if (cordsX <= -border.big && -border.small <= cordsY && cordsY <= border.small) return CircleMenuDirection.Left
        return null
    }

    private fun executeAction(
        action: CircleMenuAction,
        direction: CircleMenuDirection
    ) {
        when (action.type) {

            CircleMenuActionTypes.OpenCircleMenu -> {
                setNewCircleMenuOffset(direction = direction)
                val openCircleMenu = action.data.getAs(OpenCircleMenu::class.java)
                var circleMenuForCheck =
                    mainAppVM.allCircleMenu.value?.find { it.id == openCircleMenu.id }
                        ?: mainAppVM.allCircleMenu.value?.find { it.id == 0 }
                circleMenuForCheck?.let { menu ->
                    circleMenuForCheck =
                        if (checkCircleMenuUseCase.invoke(menu)) {
                            menu
                        } else {
                            mainAppVM.allCircleMenu.value?.find { it.id == 0 }
                        }
                }
                setCircleMenu(circleMenuForCheck)
                vibrator.vibrate(20)
            }

            CircleMenuActionTypes.OpenSettings -> {
                _circleMenuOffset.value = null
                openSettingsUseCase.invoke()
            }

            CircleMenuActionTypes.OpenApp -> {
                val currentApp = action.data.getAs(OpenApp::class.java)
                openAppUseCase.invoke(currentApp.packageName)
            }

            CircleMenuActionTypes.FlashLightOn -> {
                flashLightUseCase.on()
                mainAppVM.flashLightCondition = true
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.FlashLightOff -> {
                flashLightUseCase.off()
                mainAppVM.flashLightCondition = false
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                if (mainAppVM.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                mainAppVM.flashLightCondition = !mainAppVM.flashLightCondition
                _circleMenuOffset.value = null
            }
        }
    }

    fun executeAction(
        action: CircleMenuAction
    ) {
        when (action.type) {

            CircleMenuActionTypes.OpenSettings -> {
                _circleMenuOffset.value = null
                openSettingsUseCase.invoke()
            }

            CircleMenuActionTypes.OpenApp -> {
                val currentApp = action.data.getAs(OpenApp::class.java)
                openAppUseCase.invoke(currentApp.packageName)
            }

            CircleMenuActionTypes.FlashLightOn -> {
                flashLightUseCase.on()
                mainAppVM.flashLightCondition = true
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.FlashLightOff -> {
                flashLightUseCase.off()
                mainAppVM.flashLightCondition = false
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                if (mainAppVM.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                mainAppVM.flashLightCondition = !mainAppVM.flashLightCondition
                _circleMenuOffset.value = null
            }

            else -> {}
        }
    }

    private fun setNewCircleMenuOffset(direction: CircleMenuDirection) {
        _circleMenuOffset.value?.let { circleMenuOffset ->
            val newOffset = menuSize / 2 - (menuSize / 6 + menuSize / 10)
            val offset = when (direction) {
                CircleMenuDirection.Up -> {
                    Offset(
                        x = 0f,
                        y = -newOffset
                    )
                }

                CircleMenuDirection.Down -> {
                    Offset(
                        x = 0f,
                        y = newOffset
                    )
                }

                CircleMenuDirection.Right -> {
                    Offset(
                        x = newOffset,
                        y = 0f
                    )
                }

                CircleMenuDirection.Left -> {
                    Offset(
                        x = -newOffset,
                        y = 0f
                    )
                }
            }
            _circleMenuOffset.value = circleMenuOffset.copy(
                start = Offset(
                    x = circleMenuOffset.start.x + offset.x,
                    y = circleMenuOffset.start.y + offset.y
                )
            )
        }
    }
}
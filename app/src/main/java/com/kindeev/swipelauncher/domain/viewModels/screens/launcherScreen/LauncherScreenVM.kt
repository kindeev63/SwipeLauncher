package com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen

import android.content.Context
import android.os.Vibrator
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.entities.CircleMenuWithOffset
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.FlashLightUseCase
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.utils.getCircleMenuItem
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenUrlUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.TelephoneUseCase

class LauncherScreenVM(context: Context) : ViewModel() {
    private val _currentMenu = MutableLiveData(
        LauncherData.allCircleMenus.value?.find { it.id == 0 }?.let {
            CircleMenuWithOffset(it, null)
        }
    )
    val currentMenu: LiveData<CircleMenuWithOffset?> = _currentMenu
    private val _screenState = MutableLiveData(LauncherScreenState.SwipeBox)
    val screenState: LiveData<LauncherScreenState> = _screenState
    private val density = context.resources.displayMetrics.density
    private var clickTime = 0L

    private val userImagesUseCase = UserImagesUseCase(context)
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)
    private val checkCircleMenuUseCase = CheckCircleMenuUseCase(userImagesUseCase, applicationsUseCase)
    private val telephoneUseCase = TelephoneUseCase(context)
    private val openSettingsUseCase = OpenSettingsUseCase(context)


    private val flashLightUseCase = FlashLightUseCase(context)
    private val openUrlUseCase = OpenUrlUseCase(context)
    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val menuSize = Constants.minScreenLength / 3 * 2

    private var actionInProgress = false

    fun setCircleMenu(circleMenu: CircleMenu) {
        _currentMenu.postValue(_currentMenu.value?.copy(circleMenu = circleMenu))
    }

    private fun getSwipeOffset(offset: Offset): Offset {
        val startOffset = currentMenu.value?.offset ?: Offset.Zero
        return Offset(
            x = offset.x - startOffset.x,
            y = offset.y - startOffset.y,
        )
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
                    _screenState.value = LauncherScreenState.SearchBox
                } else {
                    _currentMenu.postValue(_currentMenu.value?.copy(offset = offset))
                }
                clickTime = event.eventTime
                actionInProgress = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!actionInProgress) {
                    val item = currentMenu.value?.circleMenu?.getCircleMenuItem(
                        menuSize = menuSize,
                        offset = getSwipeOffset(offset)
                    )
                    item?.let {
                        actionInProgress = true
                        executeAction(it.action, offset)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                LauncherData.allCircleMenus.value?.find { it.id == 0 }?.let {
                    _currentMenu.postValue(
                        CircleMenuWithOffset(
                            circleMenu = it,
                            offset = null
                        )
                    )
                }
            }
        }
        true
    }

    fun closeSearchBox() {
        _screenState.value = LauncherScreenState.SwipeBox
    }

    fun executeAction(
        action: CircleMenuAction,
        offset: Offset? = null
    ) {
        when (action) {

            is OpenCircleMenuAction -> {
                offset?.let { newOffset ->
                    var circleMenuForCheck =
                        LauncherData.allCircleMenus.value?.find { it.id == action.id }
                            ?: LauncherData.allCircleMenus.value?.find { it.id == 0 }
                    circleMenuForCheck?.let { menu ->
                        circleMenuForCheck =
                            if (checkCircleMenuUseCase.check(
                                    menu
                                )
                            ) {
                                menu
                            } else {
                                LauncherData.allCircleMenus.value?.find { it.id == 0 }
                            }
                    }
                    circleMenuForCheck?.let {
                        _currentMenu.postValue(
                            CircleMenuWithOffset(
                                circleMenu = it,
                                offset = newOffset
                            )
                        )
                    }
                    vibrator.vibrate(20)
                    actionInProgress = false
                }
            }

            is OpenSettingsAction -> {
                openSettingsUseCase.invoke()
            }

            is OpenAppAction -> {
                applicationsUseCase.openApp(action.packageName)
            }

            is FlashLightOnAction -> {
                flashLightUseCase.on()
                LauncherData.flashLightCondition = true
            }

            is FlashLightOffAction -> {
                flashLightUseCase.off()
                LauncherData.flashLightCondition = false
            }

            is ChangeFlashLightConditionAction -> {
                if (LauncherData.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                LauncherData.flashLightCondition = !LauncherData.flashLightCondition
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
}
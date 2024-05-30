package com.kindeev.swipelauncher.domain.viewModels

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
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.CircleMenuWithOffset
import com.kindeev.swipelauncher.domain.useCases.FlashLightUseCase
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.useCases.OpenAppUseCase
import com.kindeev.swipelauncher.domain.useCases.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Call
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Dial
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.getAs
import com.kindeev.swipelauncher.domain.getCircleMenuItem
import com.kindeev.swipelauncher.domain.useCases.TelephoneUseCase

class LauncherScreenVM(context: Context) : ViewModel() {
    private val _currentMenu = MutableLiveData<CircleMenuWithOffset?>(null)
    val currentMenu: LiveData<CircleMenuWithOffset?> = _currentMenu
    private val _screenState = MutableLiveData(LauncherScreenState.SwipeBox)
    val screenState: LiveData<LauncherScreenState> = _screenState
    private val density = context.resources.displayMetrics.density
    private var clickTime = 0L
    private val checkCircleMenuUseCase = CheckCircleMenuUseCase(context)
    private val telephoneUseCase = TelephoneUseCase(context)
    private val openSettingsUseCase = OpenSettingsUseCase(context)
    private val openAppUseCase = OpenAppUseCase(context)
    private val flashLightUseCase = FlashLightUseCase(context)
    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val menuSize = Constants.minScreenLength / 3 * 2

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
            }

            MotionEvent.ACTION_MOVE -> {
                val item = currentMenu.value?.circleMenu?.getCircleMenuItem(
                    menuSize = menuSize,
                    offset = getSwipeOffset(offset)
                )
                item?.let { executeAction(it, offset) }
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

    private fun executeAction(
        item: CircleMenuItem,
        offset: Offset
    ) {
        when (item.action.type) {

            CircleMenuActionTypes.OpenCircleMenu -> {
                val openCircleMenu = item.action.data.getAs(OpenCircleMenu::class.java)
                var circleMenuForCheck =
                    LauncherData.allCircleMenus.value?.find { it.id == openCircleMenu.id }
                        ?: LauncherData.allCircleMenus.value?.find { it.id == 0 }
                circleMenuForCheck?.let { menu ->
                    circleMenuForCheck =
                        if (checkCircleMenuUseCase.invoke(menu)) {
                            menu
                        } else {
                            LauncherData.allCircleMenus.value?.find { it.id == 0 }
                        }
                }
                circleMenuForCheck?.let {
                    _currentMenu.postValue(
                        CircleMenuWithOffset(
                            circleMenu = it,
                            offset = offset
                        )
                    )
                }
                vibrator.vibrate(20)
            }

            CircleMenuActionTypes.OpenSettings -> {
                openSettingsUseCase.invoke()
            }

            CircleMenuActionTypes.OpenApp -> {
                val currentApp = item.action.data.getAs(OpenApp::class.java)
                openAppUseCase.invoke(currentApp.packageName)
            }

            CircleMenuActionTypes.FlashLightOn -> {
                flashLightUseCase.on()
                LauncherData.flashLightCondition = true
            }

            CircleMenuActionTypes.FlashLightOff -> {
                flashLightUseCase.off()
                LauncherData.flashLightCondition = false
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                if (LauncherData.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                LauncherData.flashLightCondition = !LauncherData.flashLightCondition
            }

            CircleMenuActionTypes.Call -> {
                val call = item.action.data.getAs(Call::class.java)
                telephoneUseCase.call(call.phoneNumber)
            }

            CircleMenuActionTypes.Dial -> {
                val dial = item.action.data.getAs(Dial::class.java)
                telephoneUseCase.dial(dial.phoneNumber)
            }
        }
    }

    fun executeClickOnClockAction(
        action: CircleMenuAction
    ) {
        when (action.type) {

            CircleMenuActionTypes.OpenSettings -> {
                openSettingsUseCase.invoke()
            }

            CircleMenuActionTypes.OpenApp -> {
                val currentApp = action.data.getAs(OpenApp::class.java)
                openAppUseCase.invoke(currentApp.packageName)
            }

            CircleMenuActionTypes.FlashLightOn -> {
                flashLightUseCase.on()
                LauncherData.flashLightCondition = true
            }

            CircleMenuActionTypes.FlashLightOff -> {
                flashLightUseCase.off()
                LauncherData.flashLightCondition = false
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                if (LauncherData.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                LauncherData.flashLightCondition = !LauncherData.flashLightCondition
            }
            else -> {}
        }
    }
}
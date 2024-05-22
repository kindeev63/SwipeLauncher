package com.kindeev.swipelauncher.domain.viewModels

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.kindeev.swipelauncher.domain.entities.CircleMenuOffset
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

class LauncherScreenVM(private val context: Context) : ViewModel() {
    private val _circleMenuOffset = MutableLiveData<CircleMenuOffset?>(null)
    val circleMenuOffset: LiveData<CircleMenuOffset?> = _circleMenuOffset
    private val _circleMenu = MutableLiveData<CircleMenu>()
    val circleMenu: LiveData<CircleMenu> = _circleMenu
    private val _screenState = MutableLiveData(LauncherScreenState.SwipeBox)
    val screenState: LiveData<LauncherScreenState> = _screenState
    private val density = context.resources.displayMetrics.density
    private var clickTime = 0L
    private val checkCircleMenuUseCase = CheckCircleMenuUseCase(context)
    private val openSettingsUseCase = OpenSettingsUseCase(context)
    private val openAppUseCase = OpenAppUseCase(context)
    private val flashLightUseCase = FlashLightUseCase(context)
    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val menuSize = Constants.minScreenLength / 3 * 2

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

    fun swipeOffsetInBorders(): Offset {
        circleMenuOffset.value?.let { offset ->
            val x = offset.swipe.x - offset.start.x
            val y = offset.swipe.y - offset.start.y
            val boarderOffset = menuSize / 2
            val res =  Offset(
                x = if (x > boarderOffset) {
                    boarderOffset
                } else if (x < -boarderOffset) {
                    -boarderOffset
                } else x,
                y = if (y > boarderOffset) {
                    boarderOffset
                } else if (y < -boarderOffset) {
                    -boarderOffset
                } else y,
            )
            return res
        }
        return Offset.Zero
    }

    fun setCircleMenu(circleMenu: CircleMenu?) {
        _circleMenu.value = circleMenu
    }
    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.eventTime - clickTime < 300L) {

                    // Double click
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
                    val item = circleMenu.value?.getCircleMenuItem(
                        menuSize = menuSize,
                        offset = swipeOffsetInBorders()
                    )
                    if (item == null) {
                        _circleMenuOffset.value = _circleMenuOffset.value?.copy(
                            swipe = Offset(
                                x = event.x / density,
                                y = event.y / density
                            )
                        )
                    } else {
                        executeAction(item)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                setCircleMenu(LauncherData.allCircleMenus.value?.find { it.id == 0 })
                _circleMenuOffset.value = null
            }
        }
        true
    }

    fun closeSearchBox() {
        _screenState.value = LauncherScreenState.SwipeBox
    }

    private fun executeAction(
        item: CircleMenuItem,
    ) {
        when (item.action.type) {

            CircleMenuActionTypes.OpenCircleMenu -> {
                setNewCircleMenuOffset()
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
                setCircleMenu(circleMenuForCheck)
                vibrator.vibrate(20)
            }

            CircleMenuActionTypes.OpenSettings -> {
                _circleMenuOffset.value = null
                openSettingsUseCase.invoke()
            }

            CircleMenuActionTypes.OpenApp -> {
                val currentApp = item.action.data.getAs(OpenApp::class.java)
                openAppUseCase.invoke(currentApp.packageName)
            }

            CircleMenuActionTypes.FlashLightOn -> {
                flashLightUseCase.on()
                LauncherData.flashLightCondition = true
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.FlashLightOff -> {
                flashLightUseCase.off()
                LauncherData.flashLightCondition = false
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                if (LauncherData.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                LauncherData.flashLightCondition = !LauncherData.flashLightCondition
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.Call -> {
                val call = item.action.data.getAs(Call::class.java)
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${call.phoneNumber}")
                context.startActivity(intent)
            }

            CircleMenuActionTypes.Dial -> {
                val dial = item.action.data.getAs(Dial::class.java)
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${dial.phoneNumber}")
                context.startActivity(intent)
            }
        }
    }

    fun executeClickOnClockAction(
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
                LauncherData.flashLightCondition = true
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.FlashLightOff -> {
                flashLightUseCase.off()
                LauncherData.flashLightCondition = false
                _circleMenuOffset.value = null
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                if (LauncherData.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                LauncherData.flashLightCondition = !LauncherData.flashLightCondition
                _circleMenuOffset.value = null
            }

            else -> {}
        }
    }

    private fun setNewCircleMenuOffset() {
        _circleMenuOffset.value?.let { circleMenuOffset ->
            _circleMenuOffset.value = circleMenuOffset.copy(
                start = Offset(
                    x = circleMenuOffset.swipe.x,
                    y = circleMenuOffset.swipe.y
                )
            )
        }
    }
}
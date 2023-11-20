package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.os.Vibrator
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.kindeev.swipelauncher.data.MenuImages
import com.kindeev.swipelauncher.data.MenuOffset
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu

data class CordsAndAction(val cords: Offset, val action: CircleMenuAction)
class SwipeScreenViewModel(context: Context) : ViewModel() {
    val menuSize = context.resources.configuration.screenWidthDp / 3f * 2f
    private val _circleMenu = MutableLiveData(RootCircleMenu.rootCircleMenu)
    val circleMenu: LiveData<CircleMenu> = _circleMenu
    private val _menuOffset = MutableLiveData<MenuOffset?>(null)
    val menuOffset: LiveData<MenuOffset?> = _menuOffset
    private val _menuImages = MutableLiveData<MenuImages?>(null)
    val menuImages: LiveData<MenuImages?> = _menuImages

    fun startDrag(x: Float, y: Float) {
        val offset = Offset(
            x = x,
            y = y
        )
        _menuOffset.value = MenuOffset(
            start = offset,
            swipe = offset
        )
    }

    fun setCircleMenu(circleMenu: CircleMenu) {
        _circleMenu.value = circleMenu
    }

    fun setMenuIcons(menuImages: MenuImages) {
        _menuImages.value = menuImages
    }

    fun drag(
        x: Float,
        y: Float,
        vibrator: Vibrator,
        openSettings: () -> Unit
    ) {
        menuOffset.value?.let { notNullMenuOffset ->
            val cordsAndAction = checkCords(
                cordsX = notNullMenuOffset.swipe.x - notNullMenuOffset.start.x,
                cordsY = notNullMenuOffset.swipe.y - notNullMenuOffset.start.y,
                menuItemOffset = menuSize / 3,
                menuItemSize = menuSize / 5,
            )
            when (cordsAndAction?.action?.type) {
                CircleMenuActionTypes.NoneAction -> {
                    _menuOffset.value = null
                }

                CircleMenuActionTypes.OpenCircleMenu -> {
                    menuOffset.value?.start?.let { startOffset ->
                        _menuOffset.value = menuOffset.value?.copy(
                            start = Offset(
                                x = startOffset.x + cordsAndAction.cords.x,
                                y = startOffset.y + cordsAndAction.cords.y
                            )
                        )
                        val openCircleMenu = cordsAndAction.action.data as OpenCircleMenu
                        _circleMenu.value = openCircleMenu.circleMenu
                        vibrator.vibrate(20)
                    }
                }

                CircleMenuActionTypes.OpenSettings -> {
                    _menuOffset.value = null
                    openSettings()
                }

                null -> {
                    _menuOffset.value = menuOffset.value?.copy(
                        swipe = Offset(
                            x = x,
                            y = y
                        )
                    )
                }
            }
        }

    }

    fun stopDrag(rootCircleMenu: CircleMenu) {
        _menuOffset.value = null
        _circleMenu.value = rootCircleMenu
    }

    private fun checkCords(
        cordsX: Float,
        cordsY: Float,
        menuItemOffset: Float,
        menuItemSize: Float
    ): CordsAndAction? {
        circleMenu.value?.let { circleMenu ->
            return if (-menuItemSize / 3 <= cordsX && cordsX <= menuItemSize / 3) {
                if (menuItemOffset - menuItemSize / 3 <= cordsY) {
                    CordsAndAction(
                        cords = Offset(
                            x = 0f,
                            y = menuItemOffset
                        ),
                        action = circleMenu.downAction
                    )
                } else
                    if (cordsY <= -(menuItemOffset - menuItemSize / 3)) {
                        CordsAndAction(
                            cords = Offset(
                                x = 0f,
                                y = -menuItemOffset
                            ),
                            action = circleMenu.upAction
                        )
                    } else null
            } else
                if (-menuItemSize / 3 <= cordsY && cordsY <= menuItemSize / 3) {
                    if (menuItemOffset - menuItemSize / 3 <= cordsX) {
                        CordsAndAction(
                            cords = Offset(
                                x = menuItemOffset,
                                y = 0f
                            ),
                            action = circleMenu.rightAction
                        )
                    } else
                        if (cordsX <= -(menuItemOffset - menuItemSize / 3)) {
                            CordsAndAction(
                                cords = Offset(
                                    x = -menuItemOffset,
                                    y = 0f
                                ),
                                action = circleMenu.leftAction
                            )
                        } else null
                } else null
        }
        return null
    }
}
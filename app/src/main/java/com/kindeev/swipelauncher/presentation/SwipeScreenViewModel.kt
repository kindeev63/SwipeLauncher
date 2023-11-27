package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.data.MenuOffset
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu

data class CordsAndAction(val cords: Offset, val action: CircleMenuAction)
class SwipeScreenViewModel(
    private val context: Context,
    private val mainAppViewModel: MainAppViewModel
) : ViewModel() {
    val menuSize = context.resources.configuration.screenWidthDp / 3f * 2f
    private val _circleMenu = MutableLiveData(RootCircleMenu.rootCircleMenu)
    val circleMenu: LiveData<CircleMenu> = _circleMenu
    private val _menuOffset = MutableLiveData<MenuOffset?>(null)
    val menuOffset: LiveData<MenuOffset?> = _menuOffset

    init {
        mainAppViewModel.allCircleMenu.value?.find { it.id == 0 }?.let { rootCircleMenu ->
            setCircleMenu(rootCircleMenu)
        }
    }

    fun getCenterCircleCords(
        x: Float,
        y: Float,
        boarderOffset: Float,
    ) = Offset(
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

    private fun setCircleMenu(circleMenu: CircleMenu) {
        _circleMenu.value = circleMenu
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
                        action = circleMenu.menuActions.downAction
                    )
                } else
                    if (cordsY <= -(menuItemOffset - menuItemSize / 3)) {
                        CordsAndAction(
                            cords = Offset(
                                x = 0f,
                                y = -menuItemOffset
                            ),
                            action = circleMenu.menuActions.upAction
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
                            action = circleMenu.menuActions.rightAction
                        )
                    } else
                        if (cordsX <= -(menuItemOffset - menuItemSize / 3)) {
                            CordsAndAction(
                                cords = Offset(
                                    x = -menuItemOffset,
                                    y = 0f
                                ),
                                action = circleMenu.menuActions.leftAction
                            )
                        } else null
                } else null
        }
        return null
    }

    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        val density = context.resources.displayMetrics.density
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val offset = Offset(
                    x = event.x / density,
                    y = event.y / density
                )
                _menuOffset.value = MenuOffset(
                    start = offset,
                    swipe = offset
                )
            }

            MotionEvent.ACTION_MOVE -> {
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
                                _circleMenu.value =
                                    mainAppViewModel.allCircleMenu.value?.find { it.id == openCircleMenu.id }
                                val vibrator =
                                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrator.vibrate(20)
                            }
                        }

                        CircleMenuActionTypes.OpenSettings -> {
                            _menuOffset.value = null
                            val intent = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intent)
                        }

                        null -> {
                            _menuOffset.value = menuOffset.value?.copy(
                                swipe = Offset(
                                    x = event.x / density,
                                    y = event.y / density
                                )
                            )
                        }
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // Set circleMenu to rootCircleMenu
                _circleMenu.value = mainAppViewModel.allCircleMenu.value?.find { it.id == 0 }
                    ?: RootCircleMenu.rootCircleMenu
                _menuOffset.value = null
            }
        }
        true
    }
}
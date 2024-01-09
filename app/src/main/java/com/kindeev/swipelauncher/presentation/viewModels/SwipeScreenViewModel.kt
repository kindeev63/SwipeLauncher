package com.kindeev.swipelauncher.presentation.viewModels

import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.MenuOffset
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity
import java.lang.Integer.min

class SwipeScreenViewModel(
    private val context: Context,
    private val mainAppViewModel: MainAppViewModel
) : ViewModel() {
    val menuSize = min(context.resources.configuration.screenWidthDp, context.resources.configuration.screenHeightDp) / 3 * 2f
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

    fun setCircleMenu(circleMenu: CircleMenu) {
        _circleMenu.value = circleMenu
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
                    val direction = getItemDirection(
                        menuSize = menuSize,
                        cordsX = notNullMenuOffset.swipe.x - notNullMenuOffset.start.x,
                        cordsY = notNullMenuOffset.swipe.y - notNullMenuOffset.start.y
                    )
                    when (direction) {
                        null -> {
                            _menuOffset.value = menuOffset.value?.copy(
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
                // Set circleMenu to rootCircleMenu
                _circleMenu.value = mainAppViewModel.allCircleMenu.value?.find { it.id == 0 }
                    ?: RootCircleMenu.rootCircleMenu
                _menuOffset.value = null
            }
        }
        true
    }

    private fun executeAction(
        action: CircleMenuAction,
        direction: CircleMenuDirection
    ) {
        when (action.type) {
            CircleMenuActionTypes.NoneAction -> {
                _menuOffset.value = null
            }

            CircleMenuActionTypes.OpenCircleMenu -> {
                setNewCircleMenuOffset(direction = direction)
                val openCircleMenu = action.data as OpenCircleMenu
                _circleMenu.value =
                    mainAppViewModel.allCircleMenu.value?.find { it.id == openCircleMenu.id }
                val vibrator =
                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(20)
            }

            CircleMenuActionTypes.OpenSettings -> {
                _menuOffset.value = null
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            }

            CircleMenuActionTypes.OpenApp -> {
                val currentApp = action.data as OpenApp
                val intent = context.packageManager.getLaunchIntentForPackage(currentApp.packageName)
                intent?.let { context.startActivity(it) }
            }
        }
    }

    private fun setNewCircleMenuOffset(direction: CircleMenuDirection) {
        menuOffset.value?.let { menuOffset ->
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
            _menuOffset.value = menuOffset.copy(
                start = Offset(
                    x = menuOffset.start.x + offset.x,
                    y = menuOffset.start.y + offset.y
                )
            )
        }
    }
}
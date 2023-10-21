package com.kindeev.swipelauncher

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.NoneAction
import com.kindeev.swipelauncher.domain.circleMenuActions.OpenCircleMenu

data class ScreenSizes(val width: Int, val height: Int)
data class MenuItemCords(val x: Float, val y: Float)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBox() {
    val screenSize = ScreenSizes(
        width = LocalConfiguration.current.screenWidthDp,
        height = LocalConfiguration.current.screenHeightDp
    )
    val menuSize = remember { screenSize.width / 2f }
    val menuItemSize = remember { menuSize / 5f }
    val menuItemCords = remember {
        MenuItemCords(
            x = menuSize / 3,
            y = menuSize / 3
        )
    }
    val d = LocalDensity.current
    var startMenuOffset by remember {
        mutableStateOf<Offset?>(null)
    }
    var swipeMenuOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    var circleMenuItem by remember {
        mutableStateOf(
            CircleMenu(
                upAction = OpenCircleMenu(
                    circleMenu = CircleMenu(
                        upAction = NoneAction,
                        downAction = NoneAction,
                        rightAction = NoneAction,
                        leftAction = NoneAction,
                    )
                ),
                downAction = NoneAction,
                rightAction = NoneAction,
                leftAction = NoneAction,
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        swipeMenuOffset = Offset(
                            x = event.x / d.density,
                            y = event.y / d.density
                        )
                        startMenuOffset = swipeMenuOffset
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val cordsX = swipeMenuOffset.x - (startMenuOffset?.x ?: 0f)
                        val cordsY = swipeMenuOffset.y - (startMenuOffset?.y ?: 0f)
                        val action = checkCords(
                            cordsX = cordsX,
                            cordsY = cordsY,
                            menuItemCords = menuItemCords,
                            menuItemSize = menuItemSize,
                            circleMenu = circleMenuItem
                        )
                        when(val item = action) {
                            null -> {
                                swipeMenuOffset = Offset(
                                    x = event.x / d.density,
                                    y = event.y / d.density
                                )
                            }
                            is NoneAction -> {
                                startMenuOffset = swipeMenuOffset
                            }
                            is OpenCircleMenu -> {
//                                circleMenuItem = item.circleMenu
                                startMenuOffset = swipeMenuOffset
                            }
                        }
                    }

                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                        startMenuOffset = null
                    }
                }
                return@pointerInteropFilter true
            }
    ) {
        CircleMenuUI(
            startOffset = startMenuOffset,
            swipeOffset = swipeMenuOffset,
            menuSize = menuSize
        )
    }
}

private fun checkCords(
    cordsX: Float,
    cordsY: Float,
    menuItemCords: MenuItemCords,
    menuItemSize: Float,
    circleMenu: CircleMenu
) =
    if (-menuItemSize / 2 <= cordsX && cordsX <= menuItemSize / 2) {
        if (menuItemCords.y - menuItemSize / 2 <= cordsY) {
            circleMenu.downAction
        } else
            if (cordsY <= -(menuItemCords.y - menuItemSize / 2)) {
                circleMenu.upAction
            } else null
    } else
        if (-menuItemSize / 2 <= cordsY && cordsY <= menuItemSize / 2) {
            if (menuItemCords.x - menuItemSize / 2 <= cordsX) {
                circleMenu.rightAction
            } else
                if (cordsX <= -(menuItemCords.x - menuItemSize / 2)) {
                    circleMenu.leftAction
                } else null
        } else null

@Composable
fun CircleMenuUI(
    startOffset: Offset?,
    swipeOffset: Offset?,
    menuSize: Float
) {
    if (startOffset == null) return
    if (swipeOffset == null) return
    val d = LocalDensity.current
    Box(
        modifier = Modifier
            .offset(
                x = startOffset.x.dp - (menuSize / 2).dp,
                y = startOffset.y.dp - (menuSize / 2).dp
            )
            .size(menuSize.dp)
            .drawBehind {
                drawLine(
                    color = Color.Blue,
                    start = Offset(
                        x = menuSize / 2 * d.density,
                        y = menuSize / 2 * d.density
                    ),
                    end = Offset(
                        x = (menuSize / 2 + swipeOffset.x - startOffset.x) * d.density,
                        y = (menuSize / 2 + swipeOffset.y - startOffset.y) * d.density
                    ),
                    cap = StrokeCap.Round,
                    strokeWidth = 20f
                )
            }
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = (menuSize / 3).dp,
                    y = (menuSize / 3).dp
                )
                .size((menuSize / 3).dp)
                .drawBehind {
                    drawArc(
                        color = Color.Blue,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(
                            width = 5f
                        )
                    )
                }
        )
        CircleMenuItemUI(
            offset = Offset(
                x = menuSize / 12,
                y = (menuSize / 2) - menuSize / 10
            ),
            size = menuSize / 5
        )
        CircleMenuItemUI(
            offset = Offset(
                x = menuSize - menuSize / 12 - menuSize / 5,
                y = (menuSize / 2) - menuSize / 10
            ),
            size = menuSize / 5
        )
        CircleMenuItemUI(
            offset = Offset(
                x = (menuSize / 2) - menuSize / 10,
                y = menuSize / 12
            ),
            size = menuSize / 5
        )
        CircleMenuItemUI(
            offset = Offset(
                x = (menuSize / 2) - menuSize / 10,
                y = menuSize - menuSize / 12 - menuSize / 5
            ),
            size = menuSize / 5
        )
    }
}

@Composable
fun CircleMenuItemUI(
    offset: Offset,
    size: Float,
) {
    Box(
        modifier = Modifier
            .offset(
                x = offset.x.dp,
                y = offset.y.dp
            )
            .size(size.dp)
            .drawBehind {
                drawArc(
                    color = Color.Red,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = 5f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size((size / 5 * 4).dp),
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null
        )
    }
}
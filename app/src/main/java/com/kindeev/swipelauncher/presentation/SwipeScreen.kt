package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.CircleMenuItemAction
import com.kindeev.swipelauncher.data.circleMenuActions.NoneAction
import com.kindeev.swipelauncher.data.circleMenuActions.OpenCircleMenu
import android.os.Vibrator
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.R

data class ScreenSizes(val width: Int, val height: Int)
data class MenuItemCords(val x: Float, val y: Float)
data class CordsAndAction(val cords: Offset, val action: CircleMenuItemAction)

data class MenuIcons(
    val upIcon: Bitmap,
    val downIcon: Bitmap,
    val rightIcon: Bitmap,
    val leftIcon: Bitmap,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SwipeBox() {
    val screenSize = ScreenSizes(
        width = LocalConfiguration.current.screenWidthDp,
        height = LocalConfiguration.current.screenHeightDp
    )
    val menuSize = remember { screenSize.width / 3f * 2f }
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
                id = 0,
                upAction = NoneAction,
                downAction = NoneAction,
                rightAction = NoneAction,
                leftAction = NoneAction,
            )
        )
    }
    val vibrator = LocalContext.current.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
                        val cordsAndAction = checkCords(
                            cordsX = cordsX,
                            cordsY = cordsY,
                            menuItemCords = menuItemCords,
                            menuItemSize = menuItemSize,
                            circleMenu = circleMenuItem
                        )
                        when (cordsAndAction?.action) {
                            null -> {
                                swipeMenuOffset = Offset(
                                    x = event.x / d.density,
                                    y = event.y / d.density
                                )
                            }

                            is NoneAction -> {
                                startMenuOffset?.let { cords ->
                                    startMenuOffset = startMenuOffset?.copy(
                                        x = cords.x + cordsAndAction.cords.x,
                                        y = cords.y + cordsAndAction.cords.y
                                    )
                                    vibrator.vibrate(20)
                                }
                            }

                            is OpenCircleMenu -> {
                                startMenuOffset?.let { cords ->
                                    startMenuOffset = startMenuOffset?.copy(
                                        x = cords.x + cordsAndAction.cords.x,
                                        y = cords.y + cordsAndAction.cords.y
                                    )
                                    vibrator.vibrate(20)
                                }
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
        val bitmapUp = LocalContext.current.getDrawable(R.drawable.ic_up_arrow)!!.toBitmap()
        val bitmapDown = LocalContext.current.getDrawable(R.drawable.ic_down_arrow)!!.toBitmap()
        val bitmapRight = LocalContext.current.getDrawable(R.drawable.ic_right_arrow)!!.toBitmap()
        val bitmapLeft = LocalContext.current.getDrawable(R.drawable.ic_left_arrow)!!.toBitmap()
        CircleMenuUI(
            startOffset = startMenuOffset,
            swipeOffset = swipeMenuOffset,
            menuSize = menuSize,
            menuIcons = MenuIcons(
                upIcon = bitmapUp,
                downIcon = bitmapDown,
                rightIcon = bitmapRight,
                leftIcon = bitmapLeft
            )
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
    if (-menuItemSize / 3 <= cordsX && cordsX <= menuItemSize / 3) {
        if (menuItemCords.y - menuItemSize / 3 <= cordsY) {
            CordsAndAction(
                cords = Offset(
                    x=0f,
                    y=menuItemCords.y
                ),
                action = circleMenu.downAction
            )
        } else
            if (cordsY <= -(menuItemCords.y - menuItemSize / 3)) {
                CordsAndAction(
                    cords = Offset(
                        x=0f,
                        y=-menuItemCords.y
                    ),
                    action = circleMenu.upAction
                )
            } else null
    } else
        if (-menuItemSize / 3 <= cordsY && cordsY <= menuItemSize / 3) {
            if (menuItemCords.x - menuItemSize / 3 <= cordsX) {
                CordsAndAction(
                    cords = Offset(
                        x=menuItemCords.x,
                        y=0f
                    ),
                    action = circleMenu.rightAction
                )
            } else
                if (cordsX <= -(menuItemCords.x - menuItemSize / 3)) {
                    CordsAndAction(
                        cords = Offset(
                            x=-menuItemCords.x,
                            y=0f
                        ),
                        action = circleMenu.leftAction
                    )
                } else null
        } else null


@Composable
fun CircleMenuUI(
    startOffset: Offset?,
    swipeOffset: Offset?,
    menuSize: Float,
    menuIcons: MenuIcons
) {
    if (startOffset == null) return
    if (swipeOffset == null) return
    Box(
        modifier = Modifier
            .offset(
                x = startOffset.x.dp - (menuSize / 2).dp,
                y = startOffset.y.dp - (menuSize / 2).dp
            )
            .size(menuSize.dp)
    ) {
        val cords = circleCords(
            menuSize = menuSize,
            x = swipeOffset.x - startOffset.x,
            y = swipeOffset.y - startOffset.y
        )
        Box(
            modifier = Modifier
                .offset(
                    x = cords.x.dp,
                    y = cords.y.dp
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
            size = menuSize / 5,
            bitmap = menuIcons.leftIcon
        )
        CircleMenuItemUI(
            offset = Offset(
                x = menuSize - menuSize / 12 - menuSize / 5,
                y = (menuSize / 2) - menuSize / 10
            ),
            size = menuSize / 5,
            bitmap = menuIcons.rightIcon
        )
        CircleMenuItemUI(
            offset = Offset(
                x = (menuSize / 2) - menuSize / 10,
                y = menuSize / 12
            ),
            size = menuSize / 5,
            bitmap = menuIcons.upIcon
        )
        CircleMenuItemUI(
            offset = Offset(
                x = (menuSize / 2) - menuSize / 10,
                y = menuSize - menuSize / 12 - menuSize / 5
            ),
            size = menuSize / 5,
            bitmap = menuIcons.downIcon
        )
    }
}

private fun circleCords(
    menuSize: Float,
    x: Float,
    y: Float
): Offset {
    var xCords = menuSize / 3 + x
    if (xCords < 0) {
        xCords = 0f
    }
    if (xCords > menuSize / 3 * 2) {
        xCords = menuSize / 3 * 2
    }
    var yCords = menuSize / 3 + y
    if (yCords < 0) {
        yCords = 0f
    }
    if (yCords > menuSize / 3 * 2) {
        yCords = menuSize / 3 * 2
    }
    return Offset(xCords, yCords)
}

@Composable
fun CircleMenuItemUI(
    bitmap: Bitmap,
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
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null
        )
    }
}
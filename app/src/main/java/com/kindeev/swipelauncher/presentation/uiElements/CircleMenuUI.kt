package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.presentation.SwipeScreenViewModel

@Composable
fun CircleMenuUI(
    viewModel: SwipeScreenViewModel,
    menuSize: Float,
    centerCircleColor: Color = Color.Blue,
    centerCircleStroke: Stroke = Stroke(
        width = 5f
    ),
    itemCircleColor: Color = Color.Red,
    itemCircleStroke: Stroke = Stroke(
        width = 5f
    )
) {
    val menuOffsetState = viewModel.menuOffset.observeAsState()
    val menuOffset = menuOffsetState.value ?: return
    val circleMenu = viewModel.circleMenu.observeAsState()
    val density = LocalDensity.current.density
    val imageSize = menuSize / 6
    Box(
        modifier = Modifier
            .offset(
                x = (menuOffset.start.x).dp - (menuSize / 2).dp,
                y = menuOffset.start.y.dp - (menuSize / 2).dp
            )
            .size(menuSize.dp)
    ) {
        val itemsOffsets = listOf(
            // up
            Offset(
                x = menuSize / 2,
                y = menuSize / 5.5f
            ),
            // down
            Offset(
                x = menuSize / 2,
                y = menuSize - menuSize / 5.5f
            ),
            // right
            Offset(
                x = menuSize / 5.5f,
                y = menuSize / 2
            ),
            // left
            Offset(
                x = menuSize - menuSize / 5.5f,
                y = menuSize / 2
            ),
        )
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Drawing a center circle
            drawCircle(
                center = getCenterCircleCords(
                    boarderOffset = (menuSize / 2) * density,
                    x = (menuOffset.swipe.x - menuOffset.start.x) * density,
                    y = (menuOffset.swipe.y - menuOffset.start.y) * density
                ),
                color = centerCircleColor,
                style = centerCircleStroke,
                radius = menuSize / 2
            )
            // Drawing items circles
            itemsOffsets.forEach { cords ->
                drawCircle(
                    color = itemCircleColor,
                    style = itemCircleStroke,
                    center = cords.copy(
                        x = cords.x * density,
                        y = cords.y * density
                    ),
                    radius = menuSize / 3
                )
            }
        }
        circleMenu.value?.let { menu ->
            listOf(0, 1, 2, 3).forEach { index ->
                var offset = itemsOffsets[index]
                offset = offset.copy(
                    x = offset.x - imageSize / 2,
                    y = offset.y - imageSize / 2,
                )
                Image(
                    modifier = Modifier
                        .offset(
                            x = offset.x.dp,
                            y = offset.y.dp
                        )
                        .size(imageSize.dp),
                    painter = getItemImage(
                        circleMenuImage =
                        listOf(
                            menu.directionUp.image,
                            menu.directionDown.image,
                            menu.directionRight.image,
                            menu.directionLeft.image,
                        )[index]
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun getItemImage(circleMenuImage: CircleMenuImage): Painter {
    return when (circleMenuImage.type) {
        CircleMenuImageTypes.NoneImage -> {
            painterResource(id = R.drawable.ic_settings)
        }

        CircleMenuImageTypes.DefaultImage -> {
            val defaultImage = circleMenuImage.data as DefaultImage
            painterResource(id = defaultImage.id)
        }

        CircleMenuImageTypes.AppImage -> {
            painterResource(id = R.drawable.ic_settings)
        }
    }
}

private fun getCenterCircleCords(
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
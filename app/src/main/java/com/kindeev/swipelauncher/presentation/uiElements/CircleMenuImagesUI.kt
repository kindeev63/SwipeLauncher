package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.MenuImages
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage


@Composable
fun CircleMenuImagesUI(
    menuSize: Float,
    menuImages: MenuImages,
    itemCircleColor: Color,
    itemCircleStroke: Stroke
) {
    val menuItemSize = menuSize / 6
    val itemsOffsets = getItemsOffset(menuSize = menuSize)
    Canvas(
        modifier = Modifier.size(menuSize.dp)
    ) {
        // Draw circles
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
    // Draw images
    listOf(0, 1, 2, 3).forEach { index ->
        var offset = itemsOffsets[index]
        offset = offset.copy(
            x = offset.x - menuItemSize / 2,
            y = offset.y - menuItemSize / 2,
        )
        Image(
            modifier = Modifier
                .offset(
                    x = offset.x.dp,
                    y = offset.y.dp
                )
                .size(menuItemSize.dp),
            painter = getItemImage(
                circleMenuImage =
                listOf(
                    menuImages.upImage,
                    menuImages.downImage,
                    menuImages.rightImage,
                    menuImages.leftImage,
                )[index]
            ),
            contentDescription = null
        )
    }
}

private fun getItemsOffset(menuSize: Float) =
    listOf(
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
        )
    )

@Composable
private fun getItemImage(circleMenuImage: CircleMenuImage): Painter {
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
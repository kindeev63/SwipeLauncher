package com.kindeev.swipelauncher.presentation.ui.uiElements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.dataBase.MenuImages
import com.kindeev.swipelauncher.domain.getItemImage
import com.kindeev.swipelauncher.domain.getItemsOffset

@Composable
fun CircleMenuImagesUI(
    menuSize: Float,
    menuImages: MenuImages,
    itemCircleColor: Color = Color.Red,
    itemCircleStroke: Stroke = Stroke(
        width = 5f
    )
) {
    val imagesOffset =
        getItemsOffset(menuSize = menuSize, itemSize = menuSize / 10)
    val circlesOffset = getItemsOffset(menuSize = menuSize, itemSize = 0f)
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        Canvas(
            modifier = Modifier.size(menuSize.dp)
        ) {
            // Draw circles
            circlesOffset.forEach { cords ->

                // Draw background
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    style = Fill,
                    center = cords.copy(
                        x = cords.x * density,
                        y = cords.y * density
                    ),
                    radius = menuSize * density / 10
                )

                // Draw stroke
                drawCircle(
                    color = itemCircleColor,
                    style = itemCircleStroke,
                    center = cords.copy(
                        x = cords.x * density,
                        y = cords.y * density
                    ),
                    radius = menuSize * density / 10
                )
            }
        }
        // Draw images
        listOf(0, 1, 2, 3).forEach { index ->
            val offset = imagesOffset[index]
            listOf(
                menuImages.upImage,
                menuImages.downImage,
                menuImages.rightImage,
                menuImages.leftImage,
            )[index].getItemImage(LocalContext.current)?.let { imageBitmap ->
                Image(
                    modifier = Modifier
                        .offset(
                            x = offset.x.dp,
                            y = offset.y.dp
                        )
                        .size((menuSize / 10).dp),
                    bitmap = imageBitmap,
                    contentDescription = null
                )
            }
        }
    }

}
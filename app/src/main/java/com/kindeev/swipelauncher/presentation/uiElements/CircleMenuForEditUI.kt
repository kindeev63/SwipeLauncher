package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.CircleMenuFunctions
import com.kindeev.swipelauncher.data.MenuImages

@Composable
fun CircleMenuForEditUI(
    menuSize: Float,
    menuImages: MenuImages,
    upImageClick: () -> Unit,
    downImageClick: () -> Unit,
    rightImageClick: () -> Unit,
    leftImageClick: () -> Unit,
    selectedDirection: CircleMenuDirection?
) {
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        selectedDirection?.let {
            SelectedBox(
                cords = when (selectedDirection) {
                    CircleMenuDirection.Up -> {
                        Offset(
                            x = menuSize * 3 / 8,
                            y = menuSize / 24
                        )
                    }

                    CircleMenuDirection.Down -> {
                        Offset(
                            x = menuSize * 3 / 8,
                            y = menuSize / 24 * 17
                        )
                    }

                    CircleMenuDirection.Right -> {
                        Offset(
                            x = menuSize / 24 * 17,
                            y = menuSize * 3 / 8
                        )
                    }

                    CircleMenuDirection.Left -> {
                        Offset(
                            x = menuSize / 24,
                            y = menuSize * 3 / 8
                        )
                    }
                },
                size = menuSize / 4
            )

        }
        CircleMenuImagesUI(
            menuSize = menuSize,
            menuImages = menuImages
        )
        val functions = listOf(
            upImageClick,
            downImageClick,
            rightImageClick,
            leftImageClick
        )
        val itemsOffset =
            CircleMenuFunctions.getItemsOffset(menuSize = menuSize, itemSize = menuSize / 5)
        listOf(0, 1, 2, 3).forEach { index ->
            val offset = itemsOffset[index]
            val function = functions[index]
            Box(
                modifier = Modifier
                    .offset(
                        x = offset.x.dp,
                        y = offset.y.dp,
                    )
                    .size((menuSize / 5).dp)
                    .clip(CircleShape)
                    .clickable(onClick = function)
            )
        }
    }

}

@Composable
private fun SelectedBox(
    cords: Offset,
    size: Float
) {
    val offset by animateOffsetAsState(targetValue = cords)
    Box(
        modifier = Modifier
            .offset(
                x = offset.x.dp,
                y = offset.y.dp
            )
            .size(size.dp)
            .background(
                color = Color(red = 54, green = 129, blue = 218),
                shape = RoundedCornerShape(20.dp),
            )
    )
}
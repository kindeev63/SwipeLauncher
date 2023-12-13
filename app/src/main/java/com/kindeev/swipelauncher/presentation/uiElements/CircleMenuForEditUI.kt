package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.data.MenuImages

@Composable
fun CircleMenuForEditUI(
    menuSize: Float,
    menuImages: MenuImages,
    upImageClick: () -> Unit,
    downImageClick: () -> Unit,
    rightImageClick: () -> Unit,
    leftImageClick: () -> Unit,
) {
    val itemSize = menuSize / 6
    Box(
        modifier = Modifier.size(menuSize.dp)
    ) {
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
            val itemsOffset = getItemsOffset(menuSize)
        listOf(0, 1, 2, 3).forEach { index ->
            val offset = itemsOffset[index]
            val function = functions[index]
            Box(
                modifier = Modifier
                    .offset(
                        x = (offset.x - itemSize / 2).dp,
                        y = (offset.y - itemSize / 2).dp,
                    )
                    .size((menuSize / 6).dp)
                    .clickable(onClick = function)
            )
        }
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
            x = menuSize - menuSize / 5.5f,
            y = menuSize / 2
        ),
        // left
        Offset(
            x = menuSize / 5.5f,
            y = menuSize / 2
        )
    )
package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.utils.getItemOffset

@Composable
fun CircleMenuItems(
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
    items: List<CircleMenuItem>,
    menuSize: Float
) {
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        items.forEach { item ->
            getItemImage(item.image)?.let { imageBitmap ->
                val offset = item.offset.getItemOffset(menuSize)
                Image(
                    modifier = Modifier
                        .offset(
                            x = offset.x.dp,
                            y = offset.y.dp
                        )
                        .size((menuSize / 5).dp),
                    bitmap = imageBitmap,
                    contentDescription = null
                )
            }
        }
    }
}
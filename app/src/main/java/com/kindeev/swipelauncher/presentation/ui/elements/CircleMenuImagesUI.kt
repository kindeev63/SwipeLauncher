package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.dataBase.MenuImages
import com.kindeev.swipelauncher.domain.getItemImage
import com.kindeev.swipelauncher.domain.getItemsOffset

@Composable
fun CircleMenuImagesUI(
    menuSize: Float,
    menuImages: MenuImages
) {
    val imagesOffsetList =
        getItemsOffset(menuSize = menuSize, itemSize = menuSize / 5)
    val imagesList = listOf(
        menuImages.upImage,
        menuImages.downImage,
        menuImages.rightImage,
        menuImages.leftImage,
    )
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        for (index in 0 ..3) {
            imagesList[index].getItemImage(LocalContext.current)?.let { imageBitmap ->
                val offset = imagesOffsetList[index]
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
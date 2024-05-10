package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.getItemImage
import com.kindeev.swipelauncher.domain.getItemOffset

@Composable
fun CircleMenuItems(
    items: List<CircleMenuItem>,
    menuSize: Float
) {
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        val context = LocalContext.current
        items.forEach { item ->
            item.image.getItemImage(context)?.let { imageBitmap ->
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
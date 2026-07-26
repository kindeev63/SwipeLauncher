package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw

@Composable
fun CircleMenuItems(
    modifier: Modifier,
    items: List<CircleMenuItemToDraw>,
    itemSize: Float,
) {
    Box(
        modifier = modifier
    ) {
        items.forEach { item ->
            Image(
                bitmap = item.imageBitmap,
                modifier = Modifier
                    .offset(
                        x = item.offset.x.dp,
                        y = item.offset.y.dp
                    )
                    .size(itemSize.dp),
                contentDescription = null
            )
        }
    }
}
package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kindeev.swipelauncher.data.userImages.getCoilModel
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CircleMenuItems(
    items: List<CircleMenuItem>,
    menuSize: Float
) {
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        val itemsOffset = getOffset(items.size, menuSize)
        val itemSize = getItemSize(items.size, menuSize)
        val context = LocalContext.current
        items.forEachIndexed { index, item ->
            AsyncImage(
                model = item.image.getCoilModel(context),
                modifier = Modifier
                    .offset(
                        x = itemsOffset[index].x,
                        y = itemsOffset[index].y
                    )
                    .size(itemSize.dp),
                contentDescription = null
            )
        }
    }
}

private fun getOffset(
    elementsCount: Int,
    size: Float
): List<DpOffset> {
    val alpha = 360f / elementsCount
    val itemSize = getItemSize(elementsCount, size)
    return (0 until elementsCount).map { alpha * it }.map {
        DpOffset(
            x = (size / 2 + sin((it + 0.5f * alpha + getStartOffset(elementsCount)) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
            y = (size / 2 - cos((it + 0.5f * alpha + getStartOffset(elementsCount)) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
        )
    }
}

private fun getItemSize(
    elementsCount: Int,
    size: Float
): Float {
    if (elementsCount == 0) {
        return size / 4
    }
    val value = sqrt((size / 2).pow(2) * (1 - cos(2 * PI / elementsCount))).toFloat() / 6 * 5
    return if (value > 0 && value < size / 4) {
        value
    } else {
        size / 4
    }
}

private fun getStartOffset(elementsCount: Int): Float {
    if (elementsCount == 0) {
        return 0f
    }
    return -360 / elementsCount / 2f
}


package com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.mappers

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.presentation.entities.CircleMenuForUI
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities.CircleMenuItemToDrawVM
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities.CircleMenuToDrawVM
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun List<CircleMenuForUI>.toDrawVM(menuSize: Float): Map<Int, CircleMenuToDrawVM> {
    val angles = mutableMapOf<Int, List<Float>>()
    val offsets = mutableMapOf<Int, Map<Int, Offset>>()
    val itemSizes = mutableMapOf<Int, Float>()
    return associate { menu ->
        val count = menu.items.size
        val itemSize = itemSizes[count] ?: getItemSize(count, menuSize).also {
            itemSizes[count] = it
        }
        menu.id to CircleMenuToDrawVM(
            id = menu.id,
            items = menu.items.mapIndexed { index, item ->
                CircleMenuItemToDrawVM(
                    item = CircleMenuItemToDraw(
                        image = item.imageBitmap,
                        offset = (
                                offsets[count] ?: getOffsets(count, itemSize, menuSize).also {
                                    offsets[count] = it
                                }
                                ).getValue(index)
                    ),
                    action = item.action
                )
            },
            itemSize = itemSize,
            angles = angles[count] ?: getAngles(count).also {
                angles[count] = it
            }
        )
    }
}

fun CircleMenuToDrawVM.toDraw() =
    CircleMenuToDraw(
        itemSize = itemSize,
        items = items.map { it.item }
    )

private fun getItemSize(
    count: Int,
    menuSize: Float
): Float {
    if (count == 0) {
        return menuSize / 4
    }
    val value = sqrt((menuSize / 2).pow(2) * (1 - cos(2 * PI / count))).toFloat() / 6 * 5
    return if (value > 0 && value < menuSize / 4) {
        value
    } else {
        menuSize / 4
    }
}

private fun getOffsets(count: Int, itemSize: Float, menuSize: Float): Map<Int, Offset> {
    val offsets = mutableMapOf<Int, Offset>()
    val alpha = 360f / count
    val startOffset = -360 / count / 2f
    (0 until count).forEach { index ->
        offsets[index] = Offset(
            x = menuSize / 2 + sin((alpha * (index + 0.5f) + startOffset) * PI / 180f).toFloat() * (menuSize / 2 - itemSize / 2) - itemSize / 2,
            y = menuSize / 2 - cos((alpha * (index + 0.5f) + startOffset) * PI / 180f).toFloat() * (menuSize / 2 - itemSize / 2) - itemSize / 2
        )
    }
    return offsets
}

private fun getAngles(count: Int): List<Float> =
    if (count == 0) {
        emptyList()
    } else {
        val startOffset = -360 / count / 2f
        (1..count).map { 360f / count * it + startOffset }
    }
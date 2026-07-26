package com.kindeev.swipelauncher.presentation.useCases.menuParameters

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDrawParameters
import com.kindeev.swipelauncher.presentation.entities.MenuSize
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

typealias ItemsCount = Int

fun makeCircleMenuParametersGenerator(itemsCount: ItemsCount): (MenuSize) -> CircleMenuToDrawParameters {
    val itemSizeGenerator = itemSizeGenerator(itemsCount)
    val offsetsGenerator = offsetsGenerator(itemsCount, itemSizeGenerator)
    return { menuSize ->
        CircleMenuToDrawParameters(
            offsets = offsetsGenerator(menuSize),
            itemSize = itemSizeGenerator(menuSize)
        )
    }
}

private fun offsetsGenerator(
    itemsCount: ItemsCount,
    itemSizeGenerator: (MenuSize) -> Float
): (MenuSize) -> Map<Int, Offset> {
    val pairs = buildMap {
        (0 until itemsCount).forEach { index ->
            this[index] = Pair(
                first = (1 + sin((360 / itemsCount * index) * PI / 180f)).toFloat() / 2,
                second = (1 - cos((360 / itemsCount * index) * PI / 180f)).toFloat() / 2,
            )
        }
    }
    return { menuSize ->
        val itemSize = itemSizeGenerator(menuSize)
        pairs.mapValues { (_, value) ->
            Offset(
                x = (menuSize - itemSize) * value.first,
                y = (menuSize - itemSize) * value.second
            )
        }
    }
}

private fun itemSizeGenerator(itemsCount: ItemsCount): (MenuSize) -> Float {
    if (itemsCount == 0) {
        return { menuSize ->
            menuSize / 4
        }
    }
    if (itemsCount in 2..7) {
        return { menuSize ->
            menuSize / 4
        }
    }
    val value = (sqrt(1 - cos(2 * PI / itemsCount)) / 12 * 5).toFloat()
    return { menuSize ->
        value * menuSize
    }
}

package com.kindeev.swipelauncher.domain.utils

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem

fun emptyCircleMenu(id: Int): CircleMenu {
    return CircleMenu(
        id = id,
        title = "New",
        items = emptyList()
    )
}

fun CircleMenu.getCircleMenuItem(offset: Offset, menuSize: Float): CircleMenuItem? {
    items.forEach { item ->
        val itemCords = item.offset.getItemCords(menuSize)
        for (cords in itemCords) {
            if (
                (cords.xStart == null || offset.x >= cords.xStart) && (cords.xEnd == null || offset.x <= cords.xEnd) // x
                &&
                (cords.yStart == null || offset.y >= cords.yStart) && (cords.yEnd == null || offset.y <= cords.yEnd) // y
            ) {
                return item
            }
        }
    }
    return null
}

private data class ItemCords(
    val xStart: Float?,
    val xEnd: Float?,
    val yStart: Float?,
    val yEnd: Float?
)

private fun Offset.getItemCords(menuSize: Float): List<ItemCords> {
    if (x == 0f && y == -4f) { // 1
        return listOf(
            ItemCords(
                xStart = -menuSize / 6,
                xEnd = menuSize / 6,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 5
            )
        )
    }
    if (x == 3f && y == -3f) { // 2
        return listOf(
            ItemCords(
                xStart = menuSize / 2 - menuSize / 3,
                xEnd = menuSize / 2 - menuSize / 5,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 5
            ),
            ItemCords(
                xStart = menuSize / 2 - menuSize / 5,
                xEnd = null,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 3
            ),
            ItemCords(
                xStart = menuSize / 10 * 3 - menuSize / 10,
                xEnd = menuSize / 10 * 3 + menuSize / 10,
                yStart = -menuSize / 10 * 3 - menuSize / 10,
                yEnd = -menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    if (x == 4f && y == 0f) { // 3
        return listOf(
            ItemCords(
                xStart = menuSize / 2 - menuSize / 5,
                xEnd = null,
                yStart = -menuSize / 6,
                yEnd = menuSize / 6
            )
        )
    }
    if (x == 3f && y == 3f) { // 4
        return listOf(
            ItemCords(
                xStart = menuSize / 2 - menuSize / 3,
                xEnd = menuSize / 2 - menuSize / 5,
                yStart = menuSize / 2 - menuSize / 5,
                yEnd = null
            ),
            ItemCords(
                xStart = menuSize / 2 - menuSize / 5,
                xEnd = null,
                yStart = menuSize / 2 - menuSize / 3,
                yEnd = null
            ),
            ItemCords(
                xStart = menuSize / 10 * 3 - menuSize / 10,
                xEnd = menuSize / 10 * 3 + menuSize / 10,
                yStart = menuSize / 10 * 3 - menuSize / 10,
                yEnd = menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    if (x == 0f && y == 4f) { // 5
        return listOf(
            ItemCords(
                xStart = -menuSize / 6,
                xEnd = menuSize / 6,
                yStart = menuSize / 2 - menuSize / 5,
                yEnd = null
            )
        )
    }
    if (x == -3f && y == 3f) { // 6
        return listOf(
            ItemCords(
                xStart = -menuSize / 2 + menuSize / 5,
                xEnd = -menuSize / 2 + menuSize / 3,
                yStart = menuSize / 2 - menuSize / 5,
                yEnd = null
            ),
            ItemCords(
                xStart = null,
                xEnd = -menuSize / 2 + menuSize / 5,
                yStart = menuSize / 2 - menuSize / 3,
                yEnd = null
            ),
            ItemCords(
                xStart = -menuSize / 10 * 3 - menuSize / 10,
                xEnd = -menuSize / 10 * 3 + menuSize / 10,
                yStart = menuSize / 10 * 3 - menuSize / 10,
                yEnd = menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    if (x == -4f && y == 0f) { // 7
        return listOf(
            ItemCords(
                xStart = null,
                xEnd = -menuSize / 2 + menuSize / 5,
                yStart = -menuSize / 6,
                yEnd = menuSize / 6
            )
        )
    }
    if (x == -3f && y == -3f) { // 8
        return listOf(
            ItemCords(
                xStart = -menuSize / 2 + menuSize / 5,
                xEnd = -menuSize / 2 + menuSize / 3,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 5
            ),
            ItemCords(
                xStart = null,
                xEnd = -menuSize / 2 + menuSize / 5,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 3
            ),
            ItemCords(
                xStart = -menuSize / 10 * 3 - menuSize / 10,
                xEnd = -menuSize / 10 * 3 + menuSize / 10,
                yStart = -menuSize / 10 * 3 - menuSize / 10,
                yEnd = -menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    return emptyList()
}
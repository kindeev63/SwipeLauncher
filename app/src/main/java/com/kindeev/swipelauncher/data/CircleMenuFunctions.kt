package com.kindeev.swipelauncher.data

import androidx.compose.ui.geometry.Offset

object CircleMenuFunctions {
    fun getItemsOffset(menuSize: Float, itemSize: Float) =
        listOf(
            // up
            Offset(
                x = menuSize / 2 - itemSize / 2,
                y = menuSize / 6 - itemSize / 2
            ),
            // down
            Offset(
                x = menuSize / 2 - itemSize / 2,
                y = menuSize / 6 * 5 - itemSize / 2
            ),
            // right
            Offset(
                x = menuSize / 6 * 5 - itemSize / 2,
                y = menuSize / 2 - itemSize / 2
            ),
            // left
            Offset(
                x = menuSize / 6 - itemSize / 2,
                y = menuSize / 2 - itemSize / 2
            )
        )
}
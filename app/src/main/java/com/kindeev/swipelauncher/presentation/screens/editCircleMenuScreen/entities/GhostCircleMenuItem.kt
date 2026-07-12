package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

data class GhostCircleMenuItem(
    val index: Int?,
    val image: ImageBitmap,
    val offset: Offset,
    val firstOffset: Offset,
    val size: Float
)

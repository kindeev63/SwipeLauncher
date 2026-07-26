package com.kindeev.swipelauncher.presentation.entities

import androidx.compose.ui.geometry.Offset

typealias MenuSize = Float

data class CircleMenuToDrawParameters(
    val offsets:  Map<Int, Offset>,
    val itemSize: Float,
)
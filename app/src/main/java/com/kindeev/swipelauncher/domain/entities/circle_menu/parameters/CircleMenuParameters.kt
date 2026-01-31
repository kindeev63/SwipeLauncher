package com.kindeev.swipelauncher.domain.entities.circle_menu.parameters

data class CircleMenuParameters(
    val itemsCount: Int,
    val itemSize: Float,
    val offsets: List<CircleMenuItemOffset>
)
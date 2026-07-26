package com.kindeev.swipelauncher.presentation.entities

data class CircleMenuToDraw(
    val id: Int,
    val title: String,
    val menuSize: Float,
    val itemSize: Float,
    val items: List<CircleMenuItemToDraw>
)
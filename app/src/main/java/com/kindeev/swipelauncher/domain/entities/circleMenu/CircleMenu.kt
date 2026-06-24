package com.kindeev.swipelauncher.domain.entities.circleMenu

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem

data class CircleMenu(
    val id: Int = 0,
    val title: String,
    val items: List<CircleMenuItem>
)
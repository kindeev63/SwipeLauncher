package com.kindeev.swipelauncher.domain.entities.circle_menu

import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.CircleMenuItem

data class CircleMenu(
    val id: Int = 0,
    val title: String,
    val items: List<CircleMenuItem>
)
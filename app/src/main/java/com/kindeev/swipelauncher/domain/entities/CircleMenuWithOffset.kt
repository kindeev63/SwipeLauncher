package com.kindeev.swipelauncher.domain.entities

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu

data class CircleMenuWithOffset(val circleMenu: CircleMenu, val offset: Offset?)

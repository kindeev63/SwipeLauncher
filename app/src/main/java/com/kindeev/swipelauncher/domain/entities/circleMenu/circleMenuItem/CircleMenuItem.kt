package com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage

data class CircleMenuItem(
    val image: CircleMenuImage,
    val action: CircleMenuAction,
)
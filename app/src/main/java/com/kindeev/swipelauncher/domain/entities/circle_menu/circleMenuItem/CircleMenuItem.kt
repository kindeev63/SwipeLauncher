package com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem

import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuImage.CircleMenuImage
import java.io.Serializable

data class CircleMenuItem(
    val image: CircleMenuImage,
    val action: CircleMenuAction,
): Serializable
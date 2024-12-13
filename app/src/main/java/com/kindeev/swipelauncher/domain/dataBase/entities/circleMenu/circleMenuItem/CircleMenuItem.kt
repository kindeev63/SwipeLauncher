package com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem

import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import java.io.Serializable

data class CircleMenuItem(
    val image: CircleMenuImage,
    val action: CircleMenuAction,
): Serializable
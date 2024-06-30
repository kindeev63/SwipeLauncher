package com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import java.io.Serializable

data class CircleMenuItem(
    val offset: Offset,
    val image: CircleMenuImage,
    val action: CircleMenuAction,
): Serializable
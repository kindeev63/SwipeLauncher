package com.kindeev.swipelauncher.data.entities.circleMenu

import com.kindeev.swipelauncher.data.entities.circleMenu.actions.CircleMenuEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.images.CircleMenuEntityImage
import kotlinx.serialization.Serializable

@Serializable
data class CircleMenuEntityItem(
    val image: CircleMenuEntityImage,
    val action: CircleMenuEntityAction
)
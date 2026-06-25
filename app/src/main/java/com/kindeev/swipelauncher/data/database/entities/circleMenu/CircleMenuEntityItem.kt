package com.kindeev.swipelauncher.data.database.entities.circleMenu

import com.kindeev.swipelauncher.data.database.entities.circleMenu.actions.CircleMenuEntityAction
import com.kindeev.swipelauncher.data.database.entities.circleMenu.images.CircleMenuEntityImage
import kotlinx.serialization.Serializable

@Serializable
data class CircleMenuEntityItem(
    val image: CircleMenuEntityImage,
    val action: CircleMenuEntityAction
)
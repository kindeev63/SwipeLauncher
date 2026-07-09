package com.kindeev.swipelauncher.data.entities

import com.kindeev.swipelauncher.data.entities.actions.CircleMenuEntityAction
import com.kindeev.swipelauncher.data.entities.images.CircleMenuEntityImage
import kotlinx.serialization.Serializable

@Serializable
data class CircleMenuItemEntity(
    val image: CircleMenuEntityImage,
    val action: CircleMenuEntityAction
)
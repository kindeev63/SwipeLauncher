package com.kindeev.swipelauncher.data.database.entities.circleMenu.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("default")
data class DefaultEntityImage(val data: DefaultEntityImages): CircleMenuEntityImage

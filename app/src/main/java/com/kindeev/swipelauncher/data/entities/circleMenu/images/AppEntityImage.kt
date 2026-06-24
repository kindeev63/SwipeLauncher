package com.kindeev.swipelauncher.data.entities.circleMenu.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("app")
data class AppEntityImage(val packageName: String): CircleMenuEntityImage
package com.kindeev.swipelauncher.data.database.entities.circleMenu.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("app")
data class AppEntityImage(val packageName: String): CircleMenuEntityImage
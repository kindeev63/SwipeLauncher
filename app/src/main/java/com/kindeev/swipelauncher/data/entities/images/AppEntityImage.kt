package com.kindeev.swipelauncher.data.entities.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("app")
data class AppEntityImage(val packageName: String): CircleMenuEntityImage
package com.kindeev.swipelauncher.data.entities.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("user")
data class UserEntityImage(val id: Int): CircleMenuEntityImage

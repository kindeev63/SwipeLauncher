package com.kindeev.swipelauncher.data.entities.circleMenu.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("open_url")
data class OpenUrlEntityAction(val url: String): CircleMenuEntityAction
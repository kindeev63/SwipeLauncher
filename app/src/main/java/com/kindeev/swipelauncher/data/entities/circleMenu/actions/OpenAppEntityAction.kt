package com.kindeev.swipelauncher.data.entities.circleMenu.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("open_app")
data class OpenAppEntityAction(val packageName: String): CircleMenuEntityAction

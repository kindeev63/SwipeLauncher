package com.kindeev.swipelauncher.data.database.entities.circleMenu.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("open_circle_menu")
data class OpenCircleMenuEntityAction(val id: Int): CircleMenuEntityAction
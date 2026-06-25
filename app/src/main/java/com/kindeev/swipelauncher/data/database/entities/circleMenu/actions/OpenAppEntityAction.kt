package com.kindeev.swipelauncher.data.database.entities.circleMenu.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("open_app")
data class OpenAppEntityAction(val packageName: String): CircleMenuEntityAction

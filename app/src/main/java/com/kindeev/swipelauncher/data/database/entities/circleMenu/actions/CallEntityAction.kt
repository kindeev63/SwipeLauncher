package com.kindeev.swipelauncher.data.database.entities.circleMenu.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("call")
data class CallEntityAction(val phoneNumber: String): CircleMenuEntityAction

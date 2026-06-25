package com.kindeev.swipelauncher.data.database.entities.circleMenu.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dial")
data class DialEntityAction(val phoneNumber: String): CircleMenuEntityAction

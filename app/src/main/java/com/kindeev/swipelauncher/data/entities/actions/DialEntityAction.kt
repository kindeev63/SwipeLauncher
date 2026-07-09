package com.kindeev.swipelauncher.data.entities.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("dial")
data class DialEntityAction(val phoneNumber: String): CircleMenuEntityAction

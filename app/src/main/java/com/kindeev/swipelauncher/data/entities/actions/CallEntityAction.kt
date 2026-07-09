package com.kindeev.swipelauncher.data.entities.actions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("call")
data class CallEntityAction(val phoneNumber: String): CircleMenuEntityAction

package com.kindeev.swipelauncher.data.entities.settings

import com.kindeev.swipelauncher.data.entities.circleMenu.actions.CircleMenuEntityAction
import kotlinx.serialization.Serializable

@Serializable
data class ClickOnClockEntity(
    val enable: Boolean,
    val action: CircleMenuEntityAction
)

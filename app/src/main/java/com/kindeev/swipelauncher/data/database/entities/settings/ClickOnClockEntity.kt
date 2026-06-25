package com.kindeev.swipelauncher.data.database.entities.settings

import com.kindeev.swipelauncher.data.database.entities.circleMenu.actions.CircleMenuEntityAction
import kotlinx.serialization.Serializable

@Serializable
data class ClickOnClockEntity(
    val enable: Boolean,
    val action: CircleMenuEntityAction
)

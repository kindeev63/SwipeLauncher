package com.kindeev.swipelauncher.domain.entities.settings

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction

data class ClickOnClock(
    val enable: Boolean,
    val action: CircleMenuAction
)

package com.kindeev.swipelauncher.domain.dataBase

import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import java.io.Serializable

data class MenuActions(
    var upAction: CircleMenuAction,
    var downAction: CircleMenuAction,
    var rightAction: CircleMenuAction,
    var leftAction: CircleMenuAction,
): Serializable

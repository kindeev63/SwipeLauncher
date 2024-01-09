package com.kindeev.swipelauncher.data.dataBaseElements

import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import java.io.Serializable

data class MenuActions(
    var upAction: CircleMenuAction,
    var downAction: CircleMenuAction,
    var rightAction: CircleMenuAction,
    var leftAction: CircleMenuAction,
): Serializable

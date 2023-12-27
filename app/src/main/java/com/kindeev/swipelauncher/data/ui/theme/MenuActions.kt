package com.kindeev.swipelauncher.data.ui.theme

import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import java.io.Serializable

data class MenuActions(
    var upAction: CircleMenuAction,
    var downAction: CircleMenuAction,
    var rightAction: CircleMenuAction,
    var leftAction: CircleMenuAction,
): Serializable

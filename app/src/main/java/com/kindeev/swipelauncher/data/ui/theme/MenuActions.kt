package com.kindeev.swipelauncher.data.ui.theme

import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import java.io.Serializable

data class MenuActions(
    val upAction: CircleMenuAction,
    val downAction: CircleMenuAction,
    val rightAction: CircleMenuAction,
    val leftAction: CircleMenuAction,
): Serializable

package com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.actionTypes

import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.CircleMenuAction
import java.io.Serializable

data class DialAction(val phoneNumber: String): CircleMenuAction, Serializable

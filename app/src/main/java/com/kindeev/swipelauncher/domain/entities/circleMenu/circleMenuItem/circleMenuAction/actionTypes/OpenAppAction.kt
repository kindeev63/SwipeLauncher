package com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction

data class OpenAppAction(val packageName: String): CircleMenuAction

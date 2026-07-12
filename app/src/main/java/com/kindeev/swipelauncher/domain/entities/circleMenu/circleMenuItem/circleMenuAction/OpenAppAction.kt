package com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction

import java.io.Serializable

data class OpenAppAction(val packageName: String): CircleMenuAction, Serializable

package com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes

import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import java.io.Serializable

data class OpenUrlAction(val url: String): CircleMenuAction, Serializable
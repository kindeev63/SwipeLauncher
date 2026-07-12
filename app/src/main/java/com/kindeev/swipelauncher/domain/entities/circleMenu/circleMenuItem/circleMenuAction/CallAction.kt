package com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction

import java.io.Serializable

data class CallAction(val phoneNumber: String): CircleMenuAction, Serializable

package com.kindeev.swipelauncher.domain

class CircleMenuAction {
    var type: String = NONE_ACTION
    var data: Any? = null
    companion object {
        const val NONE_ACTION = "none_action"
        const val OPEN_CIRCLE_MENU = "open_circle_menu"
        const val OPEN_SETTINGS = "open_setting"
    }
}
package com.kindeev.swipelauncher.domain.entities.settings.settingTypes

import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes

class ClickOnClock(val enabled: Boolean, action: CircleMenuAction? = null) {
    val action: CircleMenuAction
    init {
        this.action = action ?: CircleMenuAction(CircleMenuActionTypes.OpenSettings)
    }
}

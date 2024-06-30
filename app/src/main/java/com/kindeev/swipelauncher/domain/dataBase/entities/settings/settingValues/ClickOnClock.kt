package com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues

import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingValue

class ClickOnClock(val enabled: Boolean, action: CircleMenuAction? = null): SettingValue {
    val action: CircleMenuAction
    init {
        this.action = action ?: OpenSettingsAction
    }
}

package com.kindeev.swipelauncher.domain.entities.settings.settingValues

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.settings.SettingValue

class ClickOnClock(val enabled: Boolean, action: CircleMenuAction? = null): SettingValue {
    val action: CircleMenuAction = action ?: OpenSettingsAction
}

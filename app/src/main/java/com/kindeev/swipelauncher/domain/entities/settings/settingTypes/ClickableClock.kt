package com.kindeev.swipelauncher.domain.entities.settings.settingTypes

import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction

data class ClickableClock(val enabled: Boolean, val circleMenuAction: CircleMenuAction? = null)

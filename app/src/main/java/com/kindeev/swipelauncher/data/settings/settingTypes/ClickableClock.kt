package com.kindeev.swipelauncher.data.settings.settingTypes

import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction

data class ClickableClock(val enabled: Boolean, val circleMenuAction: CircleMenuAction? = null)

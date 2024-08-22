package com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange

import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingValue

data class LockScreenWallpaperChange(
    val enabled: Boolean,
    val changeType: WallpaperChangeType,
    val minutes: Int = 5
) : SettingValue

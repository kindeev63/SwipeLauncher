package com.kindeev.swipelauncher.domain.entities.settings

data class LauncherSettings(
    val openLastApp: Boolean,
    val textColorOnWallpaper: Int,
    val pickAppActionWithImage: Boolean,
    val openAppWhenClickOnClock: OpenAppWhenClickOnClock,
)
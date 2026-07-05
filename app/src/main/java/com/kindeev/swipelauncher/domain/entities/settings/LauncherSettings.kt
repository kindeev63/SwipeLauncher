package com.kindeev.swipelauncher.domain.entities.settings


data class LauncherSettings(
    val blackTextColorOnWallpaper: Boolean,
    val clickOnClock: ClickOnClock,
    val openLastApp: Boolean,
    val pickAppActionWithImage: Boolean
)
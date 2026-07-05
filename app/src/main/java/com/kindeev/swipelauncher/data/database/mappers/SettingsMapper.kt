package com.kindeev.swipelauncher.data.database.mappers

import com.kindeev.swipelauncher.data.database.entities.settings.ClickOnClockEntity
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsEntity
import com.kindeev.swipelauncher.domain.entities.settings.ClickOnClock
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings

fun LauncherSettings.toEntity(): LauncherSettingsEntity =
    LauncherSettingsEntity(
        blackTextColorOnWallpaper = blackTextColorOnWallpaper,
        clickOnClock = clickOnClock.toEntity(),
        openLastApp = openLastApp,
        pickAppActionWithImage = pickAppActionWithImage
    )

fun LauncherSettingsEntity.fromEntity(): LauncherSettings =
    LauncherSettings(
        blackTextColorOnWallpaper = blackTextColorOnWallpaper,
        clickOnClock = clickOnClock.fromEntity(),
        openLastApp = openLastApp,
        pickAppActionWithImage = pickAppActionWithImage
    )

fun ClickOnClock.toEntity(): ClickOnClockEntity =
    ClickOnClockEntity(
        enable = enable,
        action = action.toEntity()
    )

fun ClickOnClockEntity.fromEntity(): ClickOnClock =
    ClickOnClock(
        enable = enable,
        action = action.fromEntity()
    )
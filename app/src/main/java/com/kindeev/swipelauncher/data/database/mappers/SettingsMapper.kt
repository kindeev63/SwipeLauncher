package com.kindeev.swipelauncher.data.database.mappers

import com.kindeev.swipelauncher.data.database.entities.settings.ClickOnClockEntity
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsTable
import com.kindeev.swipelauncher.data.entities.mappers.fromEntity
import com.kindeev.swipelauncher.data.entities.mappers.toEntity
import com.kindeev.swipelauncher.domain.entities.settings.ClickOnClock
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings

fun LauncherSettings.toTable(): LauncherSettingsTable =
    LauncherSettingsTable(
        blackTextColorOnWallpaper = blackTextColorOnWallpaper,
        clickOnClock = clickOnClock.toTable(),
        openLastApp = openLastApp,
        pickAppActionWithImage = pickAppActionWithImage,
        showKeyboardOnStartSearch = showKeyboardOnStartSearch
    )

fun LauncherSettingsTable.fromTable(): LauncherSettings =
    LauncherSettings(
        blackTextColorOnWallpaper = blackTextColorOnWallpaper,
        clickOnClock = clickOnClock.fromTable(),
        openLastApp = openLastApp,
        pickAppActionWithImage = pickAppActionWithImage,
        showKeyboardOnStartSearch = showKeyboardOnStartSearch
    )

fun ClickOnClock.toTable(): ClickOnClockEntity =
    ClickOnClockEntity(
        enable = enable,
        action = action.toEntity()
    )

fun ClickOnClockEntity.fromTable(): ClickOnClock =
    ClickOnClock(
        enable = enable,
        action = action.fromEntity()
    )
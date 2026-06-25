package com.kindeev.swipelauncher.data.database.mappers

import com.kindeev.swipelauncher.data.database.entities.circleMenu.actions.OpenSettingsEntityAction
import com.kindeev.swipelauncher.data.database.entities.settings.ClickOnClockEntity
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsEntity
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.utils.getValueOf

fun List<SettingData>.toEntity(): LauncherSettingsEntity =
    LauncherSettingsEntity(
        id = 0,
        blackTextColorOnWallpaper = getValueOf(
            SettingNames.BlackTextColorOnWallpaper,
            BlackTextColorOnWallpaper::class.java
        )?.enabled ?: true,
        clickOnClock = getValueOf(
            SettingNames.ClickOnClock,
            ClickOnClock::class.java
        )?.toEntity()
            ?: ClickOnClockEntity(
                false,
                OpenSettingsEntityAction
            ),
        openLastApp = getValueOf(
            SettingNames.OpenLastApp,
            OpenLastApp::class.java
        )?.enabled ?: true,
        pickAppActionWithImage = getValueOf(
            SettingNames.PickAppActionWithImage,
            PickAppActionWithImage::class.java
        )?.enabled ?: true
    )

fun LauncherSettingsEntity.fromEntity(): List<SettingData> =
    listOf(
        SettingData(
            name = SettingNames.BlackTextColorOnWallpaper,
            value = BlackTextColorOnWallpaper(blackTextColorOnWallpaper),
        ),
        SettingData(
            name = SettingNames.ClickOnClock,
            value = clickOnClock.fromEntity()
        ),
        SettingData(
            name = SettingNames.OpenLastApp,
            value = OpenLastApp(openLastApp)
        ),
        SettingData(
            name = SettingNames.PickAppActionWithImage,
            value = PickAppActionWithImage(pickAppActionWithImage)
        )
    )

private fun ClickOnClockEntity.fromEntity(): ClickOnClock =
    ClickOnClock(
        enabled = enable,
        action = action.fromEntity()
    )

private fun ClickOnClock.toEntity(): ClickOnClockEntity =
    ClickOnClockEntity(
        enable = enabled,
        action = action.toEntity()
    )
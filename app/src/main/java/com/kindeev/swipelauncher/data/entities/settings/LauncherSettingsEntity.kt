package com.kindeev.swipelauncher.data.entities.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class LauncherSettingsEntity(
    @PrimaryKey
    val id: Int = 0,
    val blackTextColorOnWallpaper: Boolean,
    val clickOnClock: ClickOnClockEntity,
    val openLastApp: Boolean,
    val pickAppActionWithImage: Boolean
)

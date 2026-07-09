package com.kindeev.swipelauncher.data.database.entities.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class LauncherSettingsTable(
    @PrimaryKey
    val id: Int = 0,
    val blackTextColorOnWallpaper: Boolean,
    val clickOnClock: ClickOnClockEntity,
    val openLastApp: Boolean,
    val pickAppActionWithImage: Boolean
)

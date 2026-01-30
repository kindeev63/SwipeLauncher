package com.kindeev.swipelauncher.domain.database

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val openLastApp: Flow<Boolean>
    val textColorOnWallpaper: Flow<Int>
    val pickAppActionWithImage: Flow<Boolean>
    val openAppWhenClickOnClock: Flow<String?>

    suspend fun updateOpenLastApp(value: Boolean)
    suspend fun updateTextColorOnWallpaper(value: Int)
    suspend fun updatePickAppActionWithImage(value: Boolean)
    suspend fun updateOpenAppWhenClickOnClock(value: String?)
}
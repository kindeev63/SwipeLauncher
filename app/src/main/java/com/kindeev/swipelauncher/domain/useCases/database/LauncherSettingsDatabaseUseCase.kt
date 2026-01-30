package com.kindeev.swipelauncher.domain.useCases.database

import com.kindeev.swipelauncher.data.dataBases.settings.LauncherSettingsDao
import com.kindeev.swipelauncher.data.dataBases.settings.SLauncherSettings
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings
import kotlinx.coroutines.flow.map

class LauncherSettingsDatabaseUseCase(
    private val dao: LauncherSettingsDao,
)  {

    fun getSettingsFlow() = dao.getSettingsFlow().map { it.firstOrNull()?.toLauncherSettings() }

    suspend fun getSettings() = dao.getSettings().firstOrNull()?.toLauncherSettings()

    suspend fun insertSettings(settings: LauncherSettings) {
        dao.insertSettings(settings.toSLauncherSettings())
    }

    private fun SLauncherSettings.toLauncherSettings(): LauncherSettings {
        return LauncherSettings(
            openLastApp = openLastApp,
            textColorOnWallpaper = textColorOnWallpaper,
            pickAppActionWithImage = pickAppActionWithImage,
            openAppWhenClickOnClock = openAppWhenClickOnClock,
        )
    }

    private fun LauncherSettings.toSLauncherSettings(): SLauncherSettings {
        return SLauncherSettings(
            openLastApp = openLastApp,
            textColorOnWallpaper = textColorOnWallpaper,
            pickAppActionWithImage = pickAppActionWithImage,
            openAppWhenClickOnClock = openAppWhenClickOnClock,
        )
    }
}
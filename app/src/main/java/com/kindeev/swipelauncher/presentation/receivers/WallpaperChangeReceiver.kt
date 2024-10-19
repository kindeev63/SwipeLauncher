package com.kindeev.swipelauncher.presentation.receivers

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.HomeScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.LockScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.WallpaperChangeType
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpapersUseCase
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.utils.wallpapersHomeScreenDir
import com.kindeev.swipelauncher.domain.utils.wallpapersLockScreenDir
import kotlin.concurrent.thread

class WallpaperChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val homeWallpapersUseCase = WallpapersUseCase(context, context.wallpapersHomeScreenDir())
        val lockWallpapersUseCase = WallpapersUseCase(context, context.wallpapersLockScreenDir())

        thread {
            val prefs = context.getSharedPreferences("wallpaper", Context.MODE_PRIVATE)
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (intent.action == Constants.WALLPAPER_CHANGE_INTENT_ACTION) {
                when (intent.getIntExtra(Constants.WALLPAPER_CHANGE_SCREEN_INTENT_KEY, 0)) {
                    Constants.WALLPAPER_CHANGE_HOME_SCREEN_VALUE -> {
                        changeHomeScreenWallpaper(homeWallpapersUseCase, prefs, wallpaperManager)
                    }

                    Constants.WALLPAPER_CHANGE_LOCK_SCREEN_VALUE -> {
                        changeLockScreenWallpaper(lockWallpapersUseCase, prefs, wallpaperManager)
                    }
                }
            } else {
                val homeWallpaperSetting = LauncherData.settings.value?.getValueOf(
                    SettingNames.HomeScreenWallpaperChange,
                    HomeScreenWallpaperChange::class.java
                )
                val lockWallpaperSetting = LauncherData.settings.value?.getValueOf(
                    SettingNames.LockScreenWallpaperChange,
                    LockScreenWallpaperChange::class.java
                )
                intent.action?.let { action ->
                    if (homeWallpaperSetting?.enabled == true && checkEquals(
                            homeWallpaperSetting.changeType,
                            action
                        )
                    ) {
                        changeHomeScreenWallpaper(homeWallpapersUseCase, prefs, wallpaperManager)
                    }
                    if (lockWallpaperSetting?.enabled == true && checkEquals(
                            lockWallpaperSetting.changeType,
                            action
                        )
                    ) {
                        changeLockScreenWallpaper(lockWallpapersUseCase, prefs, wallpaperManager)
                    }
                }
            }
        }
    }

    private fun checkEquals(changeType: WallpaperChangeType, action: String): Boolean {
        return if (
            (changeType == WallpaperChangeType.Unlock && action == Intent.ACTION_USER_PRESENT)
            ||
            (changeType == WallpaperChangeType.ScreenOn && action == Intent.ACTION_SCREEN_ON)
            ||
            (changeType == WallpaperChangeType.ScreenOff && action == Intent.ACTION_SCREEN_OFF)
        ) {
            return true
        } else false
    }

    private fun changeHomeScreenWallpaper(
        wallpapersUseCase: WallpapersUseCase,
        prefs: SharedPreferences,
        wallpaperManager: WallpaperManager
    ) {
        val wallpapers =
            wallpapersUseCase.getWallpapers().map { it.id }.sorted()
        val nextWallpaperId =
            if (wallpapers.lastOrNull() == prefs.getInt("home", 0)) {
                wallpapers.firstOrNull()
            } else {
                wallpapers.firstOrNull { it > prefs.getInt("home", 0) }
            }
        nextWallpaperId?.let { id ->
            wallpapersUseCase.getWallpaper(id)?.let { bitmap ->
                wallpaperManager.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )
                val editor = prefs.edit()
                editor.putInt("home", id)
                editor.apply()
            }
        }
    }

    private fun changeLockScreenWallpaper(
        wallpapersUseCase: WallpapersUseCase,
        prefs: SharedPreferences,
        wallpaperManager: WallpaperManager
    ) {
        val wallpapers =
            wallpapersUseCase.getWallpapers().map { it.id }.sorted()
        val nextWallpaperId =
            if (wallpapers.lastOrNull() == prefs.getInt("lock", 0)) {
                wallpapers.firstOrNull()
            } else {
                wallpapers.firstOrNull { it > prefs.getInt("lock", 0) }
            }
        nextWallpaperId?.let { id ->
            wallpapersUseCase.getWallpaper(id)?.let { bitmap ->
                wallpaperManager.setBitmap(
                    bitmap,
                    null,
                    true,
                    WallpaperManager.FLAG_LOCK
                )
                val editor = prefs.edit()
                editor.putInt("lock", id)
                editor.apply()
            }
        }
    }
}

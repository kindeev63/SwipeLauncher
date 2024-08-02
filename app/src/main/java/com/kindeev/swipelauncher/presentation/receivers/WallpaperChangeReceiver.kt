package com.kindeev.swipelauncher.presentation.receivers

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.HomeScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.LockScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.WallpaperChangeType
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.getWallpaper
import com.kindeev.swipelauncher.domain.getWallpapersFrom
import com.kindeev.swipelauncher.domain.wallpapersHomeScreenDir
import com.kindeev.swipelauncher.domain.wallpapersLockScreenDir

class WallpaperChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("wallpaper", Context.MODE_PRIVATE)
        val homeWallpaperSetting = LauncherData.settings.value?.getValueOf(
            SettingNames.HomeScreenWallpaperChange,
            HomeScreenWallpaperChange::class.java
        )
        val lockWallpaperSetting = LauncherData.settings.value?.getValueOf(
            SettingNames.LockScreenWallpaperChange,
            LockScreenWallpaperChange::class.java
        )
        intent.action?.let {  action ->
            val wallpaperManager = WallpaperManager.getInstance(context)
            if (homeWallpaperSetting?.enabled == true && checkEquals(homeWallpaperSetting.changeType, action)) {
                val homeScreenWallpapers =
                    getWallpapersFrom(context.wallpapersHomeScreenDir()).map { it.id }.sorted()
                val nextHomeScreenWallpaperId =
                    if (homeScreenWallpapers.lastOrNull() == prefs.getInt("home", 0)) {
                        homeScreenWallpapers.firstOrNull()
                    } else {
                        homeScreenWallpapers.firstOrNull { it > prefs.getInt("home", 0) }
                    }
                nextHomeScreenWallpaperId?.let { id ->
                    getWallpaper(context.wallpapersHomeScreenDir(), id)?.let { bitmap ->
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
            if (lockWallpaperSetting?.enabled == true && checkEquals(lockWallpaperSetting.changeType, action)) {
                val lockScreenWallpapers =
                    getWallpapersFrom(context.wallpapersLockScreenDir()).map { it.id }.sorted()
                val nextLockScreenWallpaperId =
                    if (lockScreenWallpapers.lastOrNull() == prefs.getInt("lock", 0)) {
                        lockScreenWallpapers.firstOrNull()
                    } else {
                        lockScreenWallpapers.firstOrNull { it > prefs.getInt("lock", 0) }
                    }
                nextLockScreenWallpaperId?.let { id ->
                    getWallpaper(context.wallpapersLockScreenDir(), id)?.let { bitmap ->
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
    }

    private fun checkEquals(changeType: WallpaperChangeType, action: String): Boolean {
        return if (
            (changeType == WallpaperChangeType.Unlock && action == Intent.ACTION_USER_PRESENT)
            || (changeType == WallpaperChangeType.ScreenOn && action == Intent.ACTION_SCREEN_ON)
        ) {
            return true
        } else false
    }
}

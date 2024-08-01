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
        val homeScreenWallpapers = getWallpapersFrom(context.wallpapersHomeScreenDir()).map { it.id }.sorted()
        val lockScreenWallpapers = getWallpapersFrom(context.wallpapersLockScreenDir()).map { it.id }.sorted()
        val nextHomeScreenWallpaperId = if (homeScreenWallpapers.lastOrNull() == prefs.getInt("home", 0)) {
            homeScreenWallpapers.firstOrNull()
        } else {
            homeScreenWallpapers.firstOrNull { it > prefs.getInt("home", 0)}
        }
        val nextLockScreenWallpaperId = if (lockScreenWallpapers.lastOrNull() == prefs.getInt("lock", 0)) {
            lockScreenWallpapers.firstOrNull()
        } else {
            lockScreenWallpapers.firstOrNull { it > prefs.getInt("lock", 0)}
        }
        val wallpaperManager = WallpaperManager.getInstance(context)
        val homeWallpaperSetting = LauncherData.settings.value?.getValueOf(SettingNames.HomeScreenWallpaperChange, HomeScreenWallpaperChange::class.java)
        val lockWallpaperSetting = LauncherData.settings.value?.getValueOf(SettingNames.LockScreenWallpaperChange, LockScreenWallpaperChange::class.java)
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                if (homeWallpaperSetting?.enabled == true && homeWallpaperSetting.changeType == WallpaperChangeType.ScreenOn) {
                    nextHomeScreenWallpaperId?.let { id ->
                        getWallpaper(context.wallpapersHomeScreenDir(), id)?.let { bitmap ->
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                            val editor = prefs.edit()
                            editor.putInt("home", id)
                            editor.apply()
                        }
                    }
                }
                if (lockWallpaperSetting?.enabled == true && lockWallpaperSetting.changeType == WallpaperChangeType.ScreenOn) {
                    nextLockScreenWallpaperId?.let { id ->
                        getWallpaper(context.wallpapersLockScreenDir(), id)?.let { bitmap ->
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                            val editor = prefs.edit()
                            editor.putInt("lock", id)
                            editor.apply()
                        }
                    }
                }
            }
            Intent.ACTION_USER_PRESENT -> {
                if (homeWallpaperSetting?.enabled == true && homeWallpaperSetting.changeType == WallpaperChangeType.Unlock) {
                    nextHomeScreenWallpaperId?.let { id ->
                        getWallpaper(context.wallpapersHomeScreenDir(), id)?.let { bitmap ->
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                            val editor = prefs.edit()
                            editor.putInt("home", id)
                            editor.apply()
                        }
                    }
                }
                if (lockWallpaperSetting?.enabled == true && lockWallpaperSetting.changeType == WallpaperChangeType.Unlock) {
                    nextLockScreenWallpaperId?.let { id ->
                        getWallpaper(context.wallpapersLockScreenDir(), id)?.let { bitmap ->
                            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                            val editor = prefs.edit()
                            editor.putInt("lock", id)
                            editor.apply()
                        }
                    }
                }
            }
        }
    }
}

package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.HomeScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.LockScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.WallpaperChangeType
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpaperAlarmUseCase
import com.kindeev.swipelauncher.domain.utils.getValueOf

class RebootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val wallpaperAlarmUseCase = WallpaperAlarmUseCase(context)

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val homeScreenWallpaperChangeSetting = LauncherData.settings.value?.getValueOf(SettingNames.HomeScreenWallpaperChange, HomeScreenWallpaperChange::class.java)
            val lockScreenWallpaperChangeSetting = LauncherData.settings.value?.getValueOf(SettingNames.LockScreenWallpaperChange, LockScreenWallpaperChange::class.java)
            if (homeScreenWallpaperChangeSetting?.changeType == WallpaperChangeType.Time) {
                wallpaperAlarmUseCase.setChangeHomeScreenWallpaperAlarm(homeScreenWallpaperChangeSetting.minutes)
            }
            if (lockScreenWallpaperChangeSetting?.changeType == WallpaperChangeType.Time) {
                wallpaperAlarmUseCase.setChangeHomeScreenWallpaperAlarm(lockScreenWallpaperChangeSetting.minutes)
            }
        }
    }
}
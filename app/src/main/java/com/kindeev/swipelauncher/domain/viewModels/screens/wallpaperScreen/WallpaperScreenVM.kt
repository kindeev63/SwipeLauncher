package com.kindeev.swipelauncher.domain.viewModels.screens.wallpaperScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpaperAlarmUseCase
import com.kindeev.swipelauncher.domain.useCases.wallpapers.WallpapersUseCase
import com.kindeev.swipelauncher.domain.utils.wallpapersHomeScreenDir
import com.kindeev.swipelauncher.domain.utils.wallpapersLockScreenDir

class WallpaperScreenVM(context: Context): ViewModel() {
    private val homeWallpapersUseCase = WallpapersUseCase(context, context.wallpapersHomeScreenDir())
    private val lockWallpapersUseCase = WallpapersUseCase(context, context.wallpapersLockScreenDir())
    private val wallpaperAlarmUseCase = WallpaperAlarmUseCase(context)

    fun setChangeHomeScreenWallpaperAlarm(minutes: Int) {
        wallpaperAlarmUseCase.setChangeHomeScreenWallpaperAlarm(minutes)
    }

    fun setChangeLockScreenWallpaperAlarm(minutes: Int) {
        wallpaperAlarmUseCase.setChangeLockScreenWallpaperAlarm(minutes)
    }

    fun cancelChangeHomeScreenWallpaperAlarm() {
        wallpaperAlarmUseCase.cancelChangeHomeScreenWallpaperAlarm()
    }

    fun cancelChangeLockScreenWallpaperAlarm() {
        wallpaperAlarmUseCase.cancelChangeLockScreenWallpaperAlarm()
    }

    fun getHomeScreenWallpapersCount(): Int {
        return homeWallpapersUseCase.wallpapersCount()
    }

    fun getLockScreenWallpapersCount(): Int {
        return lockWallpapersUseCase.wallpapersCount()
    }
}
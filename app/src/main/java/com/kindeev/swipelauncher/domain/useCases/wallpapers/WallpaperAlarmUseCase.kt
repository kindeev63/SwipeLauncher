package com.kindeev.swipelauncher.domain.useCases.wallpapers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.presentation.receivers.WallpaperChangeReceiver

class WallpaperAlarmUseCase(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun setChangeHomeScreenWallpaperAlarm(minutes: Int) {
        setAlarm(
            interval = minutes * 60 * 1000L,
            pendingIntent = getHomeScreenPendingIntent(Constants.WALLPAPER_CHANGE_HOME_SCREEN_VALUE)
        )
    }

    fun setChangeLockScreenWallpaperAlarm(minutes: Int) {
        setAlarm(
            interval = minutes * 60 * 1000L,
            pendingIntent = getLockScreenPendingIntent(Constants.WALLPAPER_CHANGE_LOCK_SCREEN_VALUE)
        )
    }

    fun cancelChangeHomeScreenWallpaperAlarm() {
        alarmManager.cancel(getHomeScreenPendingIntent())
    }

    fun cancelChangeLockScreenWallpaperAlarm() {
        alarmManager.cancel(getLockScreenPendingIntent())
    }

    private fun setAlarm(
        interval: Long,
        pendingIntent: PendingIntent
    ) {
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            SystemClock.currentThreadTimeMillis(),
            interval,
            pendingIntent
        )
    }

    private fun getHomeScreenPendingIntent(
        extra: Int? = null
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            Constants.WALLPAPER_CHANGE_HOME_SCREEN_VALUE,
            getIntent(extra),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getLockScreenPendingIntent(
        extra: Int? = null
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            Constants.WALLPAPER_CHANGE_LOCK_SCREEN_VALUE,
            getIntent(extra),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getIntent(extra: Int?): Intent {
        return Intent(context, WallpaperChangeReceiver::class.java).apply {
            extra?.let {
                putExtra(Constants.WALLPAPER_CHANGE_SCREEN_INTENT_KEY, extra)
            }
            action = Constants.WALLPAPER_CHANGE_INTENT_ACTION
        }
    }
}
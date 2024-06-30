package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.check
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.getAllApplicationInfo
import com.kindeev.swipelauncher.domain.getOnlyChanged
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.isAppInstalled
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


class AppsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        thread {
            val newApplicationsInfo = context.getAllApplicationInfo()
            Handler(Looper.getMainLooper()).post {
                this.goAsync()
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch {
                    LauncherData.setAllApplications(newApplicationsInfo)
                    LauncherData.allCircleMenus.value?.let { allCircleMenus ->
                        LauncherData.insertCircleMenus(allCircleMenus.getOnlyChanged(context))
                    }
                    LauncherData.settings.value?.getValueOf(SettingNames.ClickOnClock, ClickOnClock::class.java)?.action?.let { circleMenuAction ->
                        when (circleMenuAction) {
                            is OpenAppAction -> {
                                if (!context.isAppInstalled(circleMenuAction.packageName)) {
                                    Constants.defaultSettings.find { it.name == SettingNames.ClickOnClock }
                                        ?.let {
                                            LauncherData.insertSetting(it)
                                        }
                                }
                            }

                            else -> {}
                        }
                    }
                    LauncherData.allApplicationData.value?.check(newApplicationsInfo, context)
                }
            }
        }
    }
}
package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.clickableClockSettingValue
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.getAllApplicationData
import com.kindeev.swipelauncher.domain.getAs
import com.kindeev.swipelauncher.domain.getOnlyChanged
import com.kindeev.swipelauncher.domain.isAppInstalled
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


class AppsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        thread {
            val newApplicationData = context.getAllApplicationData()
            Handler(Looper.getMainLooper()).post {
                this.goAsync()
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch {
                    LauncherData.setAllApplicationData(newApplicationData)
                    LauncherData.allCircleMenus.value?.let { allCircleMenus ->
                        LauncherData.insertCircleMenus(allCircleMenus.getOnlyChanged(context))
                    }
                    LauncherData.allSettings.value?.clickableClockSettingValue()?.circleMenuAction?.let { circleMenuAction ->
                        when (circleMenuAction.type) {
                            CircleMenuActionTypes.OpenApp -> {
                                val openApp = circleMenuAction.data.getAs(OpenApp::class.java)
                                if (!context.isAppInstalled(openApp.packageName)) {
                                    Constants.defaultSettings.find { it.setting == ApplicationSetting.ClickableClock }
                                        ?.let {
                                            LauncherData.insertSetting(it)
                                        }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }
}
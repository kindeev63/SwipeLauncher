package com.kindeev.swipelauncher.presentation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase
import com.kindeev.swipelauncher.domain.utils.getValueOf
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


class AppsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val applicationsUseCase = ApplicationsUseCase(context)
        val userImagesUseCase = UserImagesUseCase(context)
        val checkCircleMenuUseCase = CheckCircleMenuUseCase(userImagesUseCase, applicationsUseCase)

        thread {
            val newApplicationsInfo = applicationsUseCase.getAllApplicationInfo()
            Handler(Looper.getMainLooper()).post {
                this.goAsync()
                @OptIn(DelicateCoroutinesApi::class)
                GlobalScope.launch {
                    LauncherData.setAllApplications(newApplicationsInfo)
                    LauncherData.insertCircleMenus(
                        checkCircleMenuUseCase.getOnlyChanged(LauncherData.allCircleMenus.value)
                    )
                    LauncherData.settings.value.getValueOf(
                        SettingNames.ClickOnClock,
                        ClickOnClock::class.java
                    )?.action?.let { circleMenuAction ->
                        when (circleMenuAction) {
                            is OpenAppAction -> {
                                if (!applicationsUseCase.isAppInstalled(circleMenuAction.packageName)) {
                                    Constants.defaultSettings.find { it.name == SettingNames.ClickOnClock }
                                        ?.let {
                                            LauncherData.insertSetting(it)
                                        }
                                }
                            }

                            else -> {}
                        }
                    }
                    applicationsUseCase.check(
                        LauncherData.allApplicationData.value,
                        newApplicationsInfo
                    )
                }
            }
        }
    }
}